package com.project.backend.controller.auth;

import com.project.backend.dto.auth.*;
import com.project.backend.service.auth.AuthService;
import com.project.backend.dto.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Vai trò: REST controller của module auth-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ auth và điều phối xử lý sang tầng bên dưới.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    

        /**
     * Chức năng: Xử lý đăng nhập người dùng.
     * URL: POST /api/auth/login
     */
@PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            log.info("Login request for email: {}", request.getEmail());
            LoginResponseDTO response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Đăng nhập thành công"));
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    

        /**
     * Chức năng: Xử lý đăng ký tenant.
     * URL: POST /api/auth/register/tenant
     */
@PostMapping("/register/tenant")
    public ResponseEntity<ApiResponse<Void>> registerTenant(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            log.info("Tenant registration request for email: {}", request.getEmail());
            authService.registerTenant(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(null, "Đăng ký tài khoản thành công"));
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    

        /**
     * Chức năng: Xử lý đăng ký host.
     * URL: POST /api/auth/register/host
     */
@PostMapping("/register/host")
    public ResponseEntity<ApiResponse<Void>> registerHost(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            log.info("Host registration request for email: {}", request.getEmail());
            authService.registerHost(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(null, "Đăng ký tài khoản thành công"));
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    

        /**
     * Chức năng: Làm mới token.
     * URL: POST /api/auth/refresh-token
     */
@PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<RefreshTokenResponseDTO>> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        try {
            log.info("Refresh token request");
            RefreshTokenResponseDTO response = authService.refreshToken(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Token đã được làm mới"));
        } catch (Exception e) {
            log.error("Refresh token failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    

        /**
     * Chức năng: Xử lý đăng xuất người dùng.
     * URL: POST /api/auth/logout
     */
@PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Authorization header không hợp lệ"));
            }
            
            String token = authHeader.substring(7); 
            log.info("Logout request");
            authService.logout(token);
            return ResponseEntity.ok(ApiResponse.success(null, "Đăng xuất thành công"));
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    

        /**
     * Chức năng: Tiếp nhận yêu cầu password.
     * URL: POST /api/auth/forgot-password
     */
@PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        try {
            log.info("Forgot password request for email: {}", request.getEmail());
            authService.forgotPassword(request);
            return ResponseEntity.ok(ApiResponse.success(null, 
                    "Yêu cầu reset mật khẩu đã được gửi. Vui lòng kiểm tra email của bạn"));
        } catch (Exception e) {
            log.error("Forgot password failed: {}", e.getMessage());
            
            return ResponseEntity.ok(ApiResponse.success(null, 
                    "Nếu email tồn tại trong hệ thống, bạn sẽ nhận được hướng dẫn reset mật khẩu"));
        }
    }

    

        /**
     * Chức năng: Đặt lại password.
     * URL: POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        try {
            log.info("Reset password request");
            authService.resetPassword(request);
            return ResponseEntity.ok(ApiResponse.success(null, "Mật khẩu đã được thay đổi thành công"));
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Reset password failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Chức năng: Đổi mật khẩu (khi đã login).
     * URL: PATCH /api/auth/change-password
     */
    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestParam Long userId,
            @Valid @RequestBody com.project.backend.dto.auth.ChangePasswordDTO request) {
        try {
            log.info("Change password request for userId: {}", userId);
            authService.changePassword(userId, request);
            return ResponseEntity.ok(ApiResponse.success(null, "Thay đổi mật khẩu thành công"));
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Change password failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }


}
