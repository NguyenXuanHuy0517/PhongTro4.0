package com.project.backend.controller.tenant;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.dto.tenant.profile.ProfileResponseDTO;
import com.project.backend.dto.tenant.profile.ProfileUpdateDTO;
import com.project.backend.service.tenant.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Vai trò: REST controller của module tenant-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ profile và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/tenant/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

        /**
     * Chức năng: Lấy dữ liệu profile.
     * URL: GET /api/tenant/profile
     */
@GetMapping
    public ResponseEntity<ApiResponse<ProfileResponseDTO>> getProfile(
            @RequestParam Long userId) {
        log.info("GET /api/tenant/profile - userId: {}", userId);
        return ResponseEntity.ok(
                ApiResponse.success(profileService.getProfile(userId)));
    }

        /**
     * Chức năng: Cập nhật profile.
     * URL: PUT /api/tenant/profile
     */
@PutMapping
    public ResponseEntity<ApiResponse<ProfileResponseDTO>> updateProfile(
            @RequestParam Long userId,
            @RequestBody ProfileUpdateDTO request) {
        log.info("PUT /api/tenant/profile - userId: {}", userId);
        return ResponseEntity.ok(
                ApiResponse.success(profileService.updateProfile(userId, request)));
    }
}
