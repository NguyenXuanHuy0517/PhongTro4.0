package com.project.backend.controller.tenant;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.service.tenant.TenantAvatarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Vai trò: REST controller của module tenant-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ avatar và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/tenant/avatar")
@RequiredArgsConstructor
public class TenantAvatarController {

    private final TenantAvatarService avatarService;

    

        /**
     * Chức năng: Thực hiện nghiệp vụ upload avatar.
     * URL: POST /api/tenant/avatar
     */
@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @RequestParam Long userId,
            @RequestParam("file") MultipartFile file) {

        log.info("POST /api/tenant/avatar - userId: {}, fileName: {}, size: {} bytes",
                userId, file.getOriginalFilename(), file.getSize());

        String avatarUrl = avatarService.updateAvatar(userId, file);

        log.info("POST /api/tenant/avatar - userId: {} cập nhật avatar thành công: {}", userId, avatarUrl);
        return ResponseEntity.ok(ApiResponse.success(avatarUrl));
    }

    

        /**
     * Chức năng: Cập nhật avatar url.
     * URL: PUT /api/tenant/avatar
     */
@PutMapping
    public ResponseEntity<ApiResponse<String>> updateAvatarUrl(
            @RequestParam Long userId,
            @RequestBody AvatarUrlRequest request) {
        log.info("PUT /api/tenant/avatar - userId: {}", userId);
        String url = avatarService.updateAvatarUrl(userId, request.getAvatarUrl());
        log.info("PUT /api/tenant/avatar - userId: {} cập nhật thành công", userId);
        return ResponseEntity.ok(ApiResponse.success(url));
    }

    

        /**
     * Chức năng: Loại bỏ avatar.
     * URL: DELETE /api/tenant/avatar
     */
@DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeAvatar(@RequestParam Long userId) {
        log.info("DELETE /api/tenant/avatar - userId: {}", userId);
        avatarService.removeAvatar(userId);
        log.info("DELETE /api/tenant/avatar - userId: {} xoá avatar thành công", userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    
    public static class AvatarUrlRequest {
        private String avatarUrl;
                /**
         * Chức năng: Lấy dữ liệu avatar url.
         * URL: REQUEST /api/tenant/avatar
         */
public String getAvatarUrl() { return avatarUrl; }
                /**
         * Chức năng: Thực hiện nghiệp vụ set avatar url.
         * URL: REQUEST /api/tenant/avatar
         */
public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    }
}
