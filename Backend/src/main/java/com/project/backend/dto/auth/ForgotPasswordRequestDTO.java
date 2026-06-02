package com.project.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Vai trò: DTO của module auth-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến forgot password để trao đổi giữa các tầng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequestDTO {
    @Email(message = "Email phải hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;
}
