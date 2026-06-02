package com.project.backend.dto.tenant.profile;

import lombok.Data;

/**
 * Vai trò: DTO của module tenant-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến profile để trao đổi giữa các tầng.
 */
@Data
public class ProfileResponseDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String idCardNumber;
    private String avatarUrl;
    private String role;
}
