package com.project.backend.dto.tenant.profile;

import lombok.Data;

/**
 * Vai trò: DTO của module tenant-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến profile update để trao đổi giữa các tầng.
 */
@Data
public class ProfileUpdateDTO {
    private String avatarUrl;
    private String fcmToken;
    private String fullName;
    private String phoneNumber;
}
