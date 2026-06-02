package com.project.backend.service.tenant;

import com.project.backend.dto.common.PagedResponse;
import com.project.backend.entity.Notification;
import com.project.backend.repository.NotificationRepository;
import com.project.backend.dto.tenant.notification.NotificationDTO;
import com.project.backend.mapper.tenant.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MyNotificationService {

    private static final Set<String> NOTIFICATION_SORT_FIELDS = Set.of("createdAt", "readAt", "type");

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public List<NotificationDTO> getMyNotifications(Long userId) {
        return notificationRepository
                .findByUser_UserIdOrderByCreatedAtDesc(userId).stream()
                .map(notificationMapper::toDTO)
                .toList();
    }

    public PagedResponse<NotificationDTO> getNotificationsPage(
            Long userId,
            Boolean isRead,
            String search,
            int page,
            int size,
            String sort
    ) {
        Page<Notification> notificationPage = notificationRepository.findPageByUserId(
                userId,
                isRead,
                normalize(search),
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), buildSort(sort))
        );
        List<NotificationDTO> items = notificationPage.getContent().stream()
                .map(notificationMapper::toDTO)
                .toList();
        return PagedResponse.from(notificationPage, items);
    }

    public List<NotificationDTO> getUnread(Long userId) {
        return notificationRepository
                .findByUser_UserIdAndIsRead(userId, false).stream()
                .map(notificationMapper::toDTO)
                .toList();
    }

    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            if (n.getUser().getUserId().equals(userId)) {
                n.setRead(true);
                n.setReadAt(LocalDateTime.now());
                notificationRepository.save(n);
            }
        });
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository
                .findByUser_UserIdAndIsRead(userId, false);
        unread.forEach(n -> {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unread);
    }

    public long countUnread(Long userId) {
        return notificationRepository.countByUser_UserIdAndIsReadFalse(userId);
    }

    private Sort buildSort(String sort) {
        String[] sortParts = (sort == null ? "" : sort).split(",", 2);
        String requestedField = sortParts.length > 0 ? sortParts[0].trim() : "";
        String field = NOTIFICATION_SORT_FIELDS.contains(requestedField) ? requestedField : "createdAt";
        Sort.Direction direction = sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1].trim())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
