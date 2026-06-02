package com.project.backend.dto.tenant.chatbot;

import lombok.Data;

/**
 * Vai trò: DTO của module tenant-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến chat để trao đổi giữa các tầng.
 */
@Data
public class ChatResponseDTO {
    private String reply;
    private String intentDetected;
    private boolean isAnomaly;
}
