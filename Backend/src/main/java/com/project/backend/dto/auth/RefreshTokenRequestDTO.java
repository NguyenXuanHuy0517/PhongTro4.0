package com.project.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vai trò: DTO của module auth-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến refresh token để trao đổi giữa các tầng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequestDTO {
    private String token;
}
