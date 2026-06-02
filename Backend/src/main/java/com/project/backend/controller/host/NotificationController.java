package com.project.backend.controller.host;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.dto.common.PagedResponse;
import com.project.backend.dto.host.notification.NotificationRequestDTO;
import com.project.backend.dto.host.notification.NotificationResponseDTO;
import com.project.backend.service.host.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/host/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> getNotifications(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getNotificationsByUser(userId)));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationResponseDTO>>> getNotificationsPaged(
            @RequestParam Long userId,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getNotificationsPageByUser(userId, isRead, search, page, size, sort)
        ));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.countUnreadByUser(userId)));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@RequestParam Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/send-to-tenant/{tenantId}")
    public ResponseEntity<ApiResponse<Void>> sendToTenant(
            @RequestParam Long hostId,
            @PathVariable Long tenantId,
            @Valid @RequestBody NotificationRequestDTO request) {
        notificationService.sendToTenant(
                tenantId,
                request.getType(),
                request.getTitle(),
                request.getBody(),
                request.getRefType(),
                request.getRefId()
        );
        return ResponseEntity.ok(ApiResponse.success(null, "Gui thong bao den tenant thanh cong"));
    }

    @PostMapping("/send-to-all")
    public ResponseEntity<ApiResponse<Void>> sendToAllTenants(
            @RequestParam Long hostId,
            @Valid @RequestBody NotificationRequestDTO request) {
        notificationService.sendToAllTenantsByHost(
                hostId,
                request.getType(),
                request.getTitle(),
                request.getBody(),
                request.getRefType(),
                request.getRefId()
        );
        return ResponseEntity.ok(ApiResponse.success(null, "Gui thong bao den tat ca tenants thanh cong"));
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendNotification(
            @RequestParam Long hostId,
            @Valid @RequestBody NotificationRequestDTO request) {
        notificationService.sendNotification(
                hostId,
                request.getTenantId(),
                request.getType(),
                request.getTitle(),
                request.getBody(),
                request.getRefType(),
                request.getRefId()
        );
        String message = request.getTenantId() != null
                ? "Gui thong bao den tenant thanh cong"
                : "Gui thong bao den tat ca tenants thanh cong";
        return ResponseEntity.ok(ApiResponse.success(null, message));
    }
}
