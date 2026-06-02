package com.project.backend.mapper.tenant;

import com.project.backend.entity.Notification;
import com.project.backend.dto.tenant.notification.NotificationDTO;
import org.springframework.stereotype.Component;

/**
 * Vai trò: Mapper của module tenant-service.
 * Chức năng: Chuyển đổi dữ liệu cho nghiệp vụ notification giữa entity và DTO.
 */
@Component("tenantNotificationMapper")
public class NotificationMapper {

        /**
     * Chức năng: Chuyển đổi dto.
     */
public NotificationDTO toDTO(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(n.getNotificationId());
        dto.setType(n.getType());
        dto.setTitle(n.getTitle());
        dto.setBody(n.getBody());
        dto.setRefType(n.getRefType());
        dto.setRefId(n.getRefId());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}
