package com.project.backend.controller.tenant;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.dto.tenant.chatbot.ChatRequestDTO;
import com.project.backend.dto.tenant.chatbot.ChatResponseDTO;
import com.project.backend.dto.tenant.chatbot.ChatHistoryDTO;
import com.project.backend.service.tenant.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vai trò: REST controller của module tenant-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ chatbot và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/tenant/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    /**
     * Chức năng: Thực hiện nghiệp vụ chat.
     * URL: POST /api/tenant/chatbot
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponseDTO>> chat(
            @RequestParam Long userId,
            @RequestBody ChatRequestDTO request) {
        log.info("POST /api/tenant/chatbot - userId: {}, message: {}",
                userId, request.getMessage());
        return ResponseEntity.ok(
                ApiResponse.success(chatbotService.chat(userId, request)));
    }

    /**
     * Chức năng: Lấy lịch sử trò chuyện.
     * URL: GET /api/tenant/chatbot/history
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ChatHistoryDTO>>> getChatHistory(
            @RequestParam Long userId) {
        log.info("GET /api/tenant/chatbot/history - userId: {}", userId);
        return ResponseEntity.ok(
                ApiResponse.success(chatbotService.getChatHistory(userId)));
    }
}

