package com.project.backend.dto.tenant.notification;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Vai trò: DTO của module tenant-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến notification để trao đổi giữa các tầng.
 */
@Data
public class NotificationDTO {
    private Long notificationId;
    private String type;
    private String title;
    private String body;
    private String refType;
    private Long refId;
    private boolean isRead;
    private LocalDateTime createdAt;
}
