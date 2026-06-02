package com.project.backend.dto.admin.host;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vai trò: DTO của module admin-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến admin host để trao đổi giữa các tầng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminHostResponseDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private boolean isActive;
    private Long totalAreas;
    private Long totalRooms;
}
