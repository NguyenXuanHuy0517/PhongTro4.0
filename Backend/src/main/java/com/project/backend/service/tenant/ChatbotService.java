package com.project.backend.service.tenant;

import com.project.backend.entity.ChatbotHistory;
import com.project.backend.entity.Contract;
import com.project.backend.entity.User;
import com.project.backend.repository.ChatbotHistoryRepository;
import com.project.backend.repository.ContractRepository;
import com.project.backend.repository.InvoiceRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.dto.tenant.chatbot.ChatRequestDTO;
import com.project.backend.dto.tenant.chatbot.ChatResponseDTO;
import com.project.backend.dto.tenant.chatbot.ChatHistoryDTO;
import com.project.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Vai trò: Service xử lý nghiệp vụ của module tenant-service.
 * Chức năng: Chứa logic xử lý liên quan đến chatbot.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;
    private final ChatbotHistoryRepository chatbotHistoryRepository;

    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Data
    public static class AIChatRequest {
        private String session_id;
        private String message;
        private AIUserContext user_context;
    }

    @Data
    public static class AIUserContext {
        private String user_id;
        private String role;
        private String khu_tro_id;
        private String phong_id;
    }

    @Data
    public static class AIChatResponse {
        private String status;
        private AIChatResponseData data;
    }

    @Data
    public static class AIChatResponseData {
        private String reply_message;
        private String intent_detected;
        private boolean is_anomaly;
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ chat.
     */
public ChatResponseDTO chat(Long userId, ChatRequestDTO request) {
        String message = request.getMessage().toLowerCase().trim();
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + userId));

        var activeContract = findActiveContract(userId);
        String phongId = "";
        String khuTroId = "";
        if (activeContract.isPresent()) {
            phongId = activeContract.get().getRoom().getRoomCode();
            khuTroId = String.valueOf(activeContract.get().getRoom().getArea().getAreaId());
        }

        String intent = "UNKNOWN";
        String reply = "Xin lỗi, tôi chưa hiểu câu hỏi của bạn.";
        boolean isAnomaly = false;

        boolean aiSuccess = false;
        try {
            AIChatRequest aiReq = new AIChatRequest();
            aiReq.setSession_id("session_" + userId);
            aiReq.setMessage(request.getMessage());
            
            AIUserContext ctx = new AIUserContext();
            ctx.setUser_id(String.valueOf(userId));
            ctx.setRole("TENANT");
            ctx.setKhu_tro_id(khuTroId);
            ctx.setPhong_id(phongId);
            aiReq.setUser_context(ctx);

            AIChatResponse aiRes = restTemplate.postForObject(aiServiceUrl + "/api/v1/chat", aiReq, AIChatResponse.class);
            if (aiRes != null && "success".equals(aiRes.getStatus()) && aiRes.getData() != null) {
                reply = aiRes.getData().getReply_message();
                intent = aiRes.getData().getIntent_detected();
                isAnomaly = aiRes.getData().is_anomaly();
                aiSuccess = true;
            }
        } catch(Exception e) {
            log.error("Lỗi khi kết nối đến AI Service: {}", e.getMessage());
        }

        if (!aiSuccess) {
            log.warn("Sử dụng Rule-based chatbot (Fallback).");
            intent = detectIntent(message);
            reply = generateReply(userId, intent, message);
        }

        ChatbotHistory history = new ChatbotHistory();
        history.setUser(user);
        history.setUserQuestion(request.getMessage());
        history.setBotResponse(reply);
        history.setIntentDetected(intent);
        chatbotHistoryRepository.save(history);

        ChatResponseDTO response = new ChatResponseDTO();
        response.setReply(reply);
        response.setIntentDetected(intent);
        response.setAnomaly(isAnomaly);
        return response;
    }

    private String detectIntent(String message) {
        if (containsAny(message, "hóa đơn", "tiền phòng", "tiền điện",
                "tiền nước", "thanh toán", "nợ", "bao nhiêu")) {
            return "Tra_Cuu_Hoa_Don";
        }
        if (containsAny(message, "hợp đồng", "ngày hết hạn", "gia hạn",
                "còn bao lâu", "hết hạn khi nào")) {
            return "Thong_Tin_Hop_Dong";
        }
        if (containsAny(message, "dịch vụ", "wifi", "gửi xe", "vệ sinh",
                "điện nước", "giá điện", "giá nước")) {
            return "Hoi_Gia_Dich_Vu";
        }
        if (containsAny(message, "giờ giấc", "quy định", "nội quy",
                "khách", "giờ đóng cửa", "giờ mở")) {
            return "Noi_Quy_Khu_Tro";
        }
        if (containsAny(message, "báo hỏng", "sửa chữa", "hỏng",
                "rò rỉ", "điện không", "nước không", "sự cố")) {
            return "Bao_Cao_Su_Co";
        }
        if (containsAny(message, "chủ trọ", "quản lý", "số điện thoại", "liên hệ")) {
            return "Lien_He_Chu_Tro";
        }
        if (containsAny(message, "xin chào", "hello", "chào", "hi")) {
            return "Default Welcome Intent";
        }
        return "UNKNOWN";
    }

    
        /**
     * Chức năng: Tạo reply.
     */
    private String generateReply(Long userId, String intent, String message) {
        return switch (intent) {
            case "Tra_Cuu_Hoa_Don" -> handleInvoiceQuery(userId);
            case "Thong_Tin_Hop_Dong" -> handleContractQuery(userId);
            case "Hoi_Gia_Dich_Vu" -> handleServiceQuery(userId);
            case "Noi_Quy_Khu_Tro" -> handleRuleQuery();
            case "Bao_Cao_Su_Co" -> handleReportIssue();
            case "Lien_He_Chu_Tro" -> "Bạn Vui lòng xem thông tin liên hệ trong mục Tài khoản hoặc Hợp đồng nhé.";
            case "Default Welcome Intent" -> "Xin chào! Tôi là trợ lý ảo của SmartRoomMS. " +
                    "Tôi có thể giúp bạn tra cứu hóa đơn, hợp đồng, " +
                    "dịch vụ, nội quy hoặc báo sự cố. Bạn cần hỗ trợ gì?";
            default -> "Xin lỗi, tôi chưa hiểu câu hỏi của bạn. " +
                    "Bạn có thể hỏi về: hóa đơn, hợp đồng, dịch vụ, " +
                    "quy định hoặc báo sự cố.";
        };
    }

        /**
     * Chức năng: Xử lý invoice query.
     */
private String handleInvoiceQuery(Long userId) {
        var invoices = invoiceRepository.findByContract_Tenant_UserId(userId);

        if (invoices.isEmpty()) {
            return "Bạn chưa có hóa đơn nào trong hệ thống.";
        }

        
        LocalDate now = LocalDate.now();
        var currentInvoice = invoices.stream()
                .filter(i -> i.getBillingMonth() == now.getMonthValue()
                        && i.getBillingYear() == now.getYear())
                .findFirst();

        if (currentInvoice.isPresent()) {
            var inv = currentInvoice.get();
            return String.format(
                    "Hóa đơn tháng %d/%d của bạn:\n" +
                            "• Tiền phòng: %,.0f đ\n" +
                            "• Tiền điện: %,.0f đ\n" +
                            "• Tiền nước: %,.0f đ\n" +
                            "• Dịch vụ: %,.0f đ\n" +
                            "• Tổng cộng: %,.0f đ\n" +
                            "• Trạng thái: %s",
                    inv.getBillingMonth(), inv.getBillingYear(),
                    inv.getRentAmount(),
                    inv.getElecAmount(),
                    inv.getWaterAmount(),
                    inv.getServiceAmount(),
                    inv.getTotalAmount(),
                    translateStatus(inv.getStatus()));
        }

        
        var unpaid = invoices.stream()
                .filter(i -> List.of("UNPAID", "OVERDUE")
                        .contains(i.getStatus()))
                .findFirst();

        if (unpaid.isPresent()) {
            var inv = unpaid.get();
            return String.format(
                    "Bạn có hóa đơn chưa thanh toán:\n" +
                            "• Kỳ: tháng %d/%d\n" +
                            "• Tổng tiền: %,.0f đ\n" +
                            "• Trạng thái: %s\n" +
                            "Vui lòng thanh toán sớm để tránh phát sinh phí.",
                    inv.getBillingMonth(), inv.getBillingYear(),
                    inv.getTotalAmount(),
                    translateStatus(inv.getStatus()));
        }

        return "Tất cả hóa đơn của bạn đã được thanh toán. " +
                "Bạn có thể xem chi tiết trong mục Hóa đơn.";
    }

        /**
     * Chức năng: Xử lý contract query.
     */
private String handleContractQuery(Long userId) {
        var active = findActiveContract(userId);

        if (active.isEmpty()) {
            return "Bạn hiện không có hợp đồng đang hiệu lực.";
        }

        var contract = active.get();
        long daysLeft = LocalDate.now()
                .until(contract.getEndDate()).getDays();

        String warning = "";
        if (daysLeft <= 30 && daysLeft >= 0) {
            warning = String.format(
                    "\n⚠️ Hợp đồng còn %d ngày nữa sẽ hết hạn. " +
                            "Vui lòng liên hệ chủ trọ để gia hạn.", daysLeft);
        }

        return String.format(
                "Thông tin hợp đồng hiện tại của bạn:\n" +
                        "• Mã HĐ: %s\n" +
                        "• Phòng: %s — %s\n" +
                        "• Giá thuê: %,.0f đ/tháng\n" +
                        "• Hiệu lực: %s → %s\n" +
                        "• Còn lại: %d ngày%s",
                contract.getContractCode(),
                contract.getRoom().getRoomCode(),
                contract.getRoom().getArea().getAreaName(),
                contract.getActualRentPrice(),
                contract.getStartDate(),
                contract.getEndDate(),
                daysLeft,
                warning);
    }

        /**
     * Chức năng: Xử lý service query.
     */
private String handleServiceQuery(Long userId) {
        var active = findActiveContract(userId);

        if (active.isEmpty()) {
            return "Bạn hiện không có hợp đồng đang hiệu lực.";
        }

        var room = active.get().getRoom();
        return String.format(
                "Thông tin giá dịch vụ phòng %s:\n" +
                        "• Giá điện: %,.0f đ/kWh\n" +
                        "• Giá nước: %,.0f đ/m³\n\n" +
                        "Để xem danh sách dịch vụ đăng ký, " +
                        "vui lòng vào mục Hợp đồng.",
                room.getRoomCode(),
                active.get().getElecPriceOverride() != null
                        ? active.get().getElecPriceOverride()
                        : room.getElecPrice(),
                active.get().getWaterPriceOverride() != null
                        ? active.get().getWaterPriceOverride()
                        : room.getWaterPrice());
    }

        /**
     * Chức năng: Xử lý rule query.
     */
private String handleRuleQuery() {
        return "Một số quy định chung của khu trọ:\n" +
                "• Giờ đóng cửa: 23:00 — mở cửa: 05:00\n" +
                "• Khách ở lại qua đêm cần đăng ký với chủ trọ\n" +
                "• Không gây ồn ào sau 22:00\n" +
                "• Giữ gìn vệ sinh chung\n\n" +
                "Để biết quy định chi tiết, " +
                "vui lòng liên hệ trực tiếp với chủ trọ.";
    }

        /**
     * Chức năng: Xử lý report issue.
     */
private String handleReportIssue() {
        return "Để báo sự cố hoặc yêu cầu sửa chữa, " +
                "vui lòng vào mục Khiếu nại & Bảo trì trong ứng dụng " +
                "và nhấn nút Gửi khiếu nại. " +
                "Chủ trọ sẽ xử lý trong thời gian sớm nhất.";
    }

    
        /**
     * Chức năng: Thực hiện nghiệp vụ contains any.
     */
private boolean containsAny(String message, String... keywords) {
        for (String kw : keywords) {
            if (message.contains(kw)) return true;
        }
        return false;
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ translate status.
     */
private String translateStatus(String status) {
        return switch (status) {
            case "DRAFT" -> "Chưa có chỉ số";
            case "UNPAID" -> "Chưa thanh toán";
            case "PAID" -> "Đã thanh toán";
            case "OVERDUE" -> "Quá hạn";
            default -> status;
        };
    }
    private java.util.Optional<Contract> findActiveContract(Long userId) {
        return contractRepository.findFirstByTenant_UserIdAndStatusOrderByStartDateDesc(userId, "ACTIVE");
    }

    /**
     * Chức năng: Lấy lịch sử chat của người dùng (tối đa 50 tin mới nhất).
     */
    public List<ChatHistoryDTO> getChatHistory(Long userId) {
        var historyList = chatbotHistoryRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);

        // Giới hạn 50 tin nhắn gần nhất và đảo ngược lại (cũ -> mới)
        var limited = historyList.stream()
                .limit(50)
                .toList();

        // Đảo lại thứ tự: cũ nhất lên trước
        var reversed = new java.util.ArrayList<>(limited);
        java.util.Collections.reverse(reversed);

        return reversed.stream().map(h -> {
            ChatHistoryDTO dto = new ChatHistoryDTO();
            dto.setChatId(h.getChatId());
            dto.setUserQuestion(h.getUserQuestion());
            dto.setBotResponse(h.getBotResponse());
            dto.setIntentDetected(h.getIntentDetected());
            dto.setCreatedAt(h.getCreatedAt());
            return dto;
        }).toList();
    }
}
