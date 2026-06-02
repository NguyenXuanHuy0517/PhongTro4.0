package com.project.backend.service.host;

import com.project.backend.dto.common.PagedResponse;
import com.project.backend.entity.Contract;
import com.project.backend.entity.Notification;
import com.project.backend.entity.User;
import com.project.backend.repository.ContractRepository;
import com.project.backend.repository.NotificationRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.dto.host.notification.NotificationResponseDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.mapper.host.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private static final Set<String> NOTIFICATION_SORT_FIELDS = Set.of("createdAt", "readAt", "type");

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final NotificationMapper notificationMapper;

    public void sendToUser(Long userId, String type, String title, String body, String refType, Long refId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay user: " + userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setRefType(refType);
        notification.setRefId(refId);
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    public List<NotificationResponseDTO> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId).stream()
                .map(notificationMapper::toDTO)
                .toList();
    }

    public PagedResponse<NotificationResponseDTO> getNotificationsPageByUser(
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
        List<NotificationResponseDTO> items = notificationPage.getContent().stream()
                .map(notificationMapper::toDTO)
                .toList();
        return PagedResponse.from(notificationPage, items);
    }

    public long countUnreadByUser(Long userId) {
        return notificationRepository.countByUser_UserIdAndIsReadFalse(userId);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        });
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUser_UserIdAndIsRead(userId, false);
        unreadNotifications.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unreadNotifications);
    }

    public void sendToTenant(Long tenantId, String type, String title, String body, String refType, Long refId) {
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay tenant: " + tenantId));

        Notification notification = new Notification();
        notification.setUser(tenant);
        notification.setType(type);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setRefType(refType);
        notification.setRefId(refId);
        notification.setRead(false);

        notificationRepository.save(notification);
        log.info("Thong bao gui den tenant {} - type: {}", tenantId, type);
    }

    public void sendToAllTenantsByHost(Long hostId, String type, String title, String body, String refType, Long refId) {
        List<Contract> contracts = contractRepository.findWithRelationsByHostIdAndStatus(hostId, "ACTIVE");
        List<Long> tenantIds = contracts.stream()
                .map(contract -> contract.getTenant().getUserId())
                .distinct()
                .toList();

        if (tenantIds.isEmpty()) {
            log.info("Khong co tenant active nao de gui thong bao cho host {}", hostId);
            return;
        }

        Map<Long, User> tenantsById = userRepository.findAllById(tenantIds).stream()
                .collect(Collectors.toMap(User::getUserId, user -> user));

        List<Notification> notifications = tenantIds.stream()
                .map(tenantsById::get)
                .filter(Objects::nonNull)
                .map(tenant -> {
                    Notification notification = new Notification();
                    notification.setUser(tenant);
                    notification.setType(type);
                    notification.setTitle(title);
                    notification.setBody(body);
                    notification.setRefType(refType);
                    notification.setRefId(refId);
                    notification.setRead(false);
                    return notification;
                })
                .toList();

        notificationRepository.saveAll(notifications);
        log.info("Gui thong bao den {} tenants cua host {}", notifications.size(), hostId);
    }

    public void sendNotification(Long hostId, Long tenantId, String type, String title, String body,
                                 String refType, Long refId) {
        if (tenantId != null) {
            sendToTenant(tenantId, type, title, body, refType, refId);
        } else {
            sendToAllTenantsByHost(hostId, type, title, body, refType, refId);
        }
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
