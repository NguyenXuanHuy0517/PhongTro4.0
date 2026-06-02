package com.project.backend.mapper.host;

import com.project.backend.entity.Notification;
import com.project.backend.dto.host.notification.NotificationResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Vai trò: Mapper của module host-service.
 * Chức năng: Chuyển đổi dữ liệu cho nghiệp vụ notification giữa entity và DTO.
 */
@Component("hostNotificationMapper")
public class NotificationMapper {

        /**
     * Chức năng: Chuyển đổi dto.
     */
public NotificationResponseDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        return new NotificationResponseDTO(
                notification.getNotificationId(),
                notification.getType(),
                notification.getTitle(),
                notification.getBody(),
                notification.getRefType(),
                notification.getRefId(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }
}
