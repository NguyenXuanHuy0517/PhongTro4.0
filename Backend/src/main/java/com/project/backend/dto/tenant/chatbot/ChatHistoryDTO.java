package com.project.backend.dto.tenant.chatbot;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Vai trò: DTO của module tenant-service.
 * Chức năng: Đóng gói dữ liệu lịch sử chat để trả về cho client.
 */
@Data
public class ChatHistoryDTO {
    private Long chatId;
    private String userQuestion;
    private String botResponse;
    private String intentDetected;
    private LocalDateTime createdAt;
}
