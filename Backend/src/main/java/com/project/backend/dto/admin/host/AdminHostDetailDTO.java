package com.project.backend.dto.admin.host;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Vai trò: DTO của module admin-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến admin host detail để trao đổi giữa các tầng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminHostDetailDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private boolean isActive;
    private Long totalAreas;
    private Long totalRooms;
    private Long activeContracts;
    private Long overdueInvoices;
    private Long roomsWithoutInvoice;
    private String latestStatusReason;  
    private LocalDateTime createdAt;
}
