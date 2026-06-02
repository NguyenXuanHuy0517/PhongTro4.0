package com.project.backend.dto.host.tenant;

import lombok.Data;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến tenant create để trao đổi giữa các tầng.
 */
@Data
public class TenantCreateDTO {
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private String idCardNumber;
}
