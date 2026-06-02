package com.project.backend.controller.host;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.service.host.HostAvatarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Vai trò: REST controller của module host-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ avatar và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/host/avatar")
@RequiredArgsConstructor
public class HostAvatarController {

    private final HostAvatarService avatarService;

    

        /**
     * Chức năng: Thực hiện nghiệp vụ upload avatar.
     * URL: POST /api/host/avatar
     */
@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @RequestParam Long userId,
            @RequestParam("file") MultipartFile file) {

        log.info("POST /api/host/avatar - userId: {}, fileName: {}, size: {} bytes",
                userId, file.getOriginalFilename(), file.getSize());

        String avatarUrl = avatarService.updateAvatar(userId, file);

        log.info("POST /api/host/avatar - userId: {} cập nhật avatar thành công: {}", userId, avatarUrl);
        return ResponseEntity.ok(ApiResponse.success(avatarUrl));
    }

    

        /**
     * Chức năng: Xóa avatar.
     * URL: DELETE /api/host/avatar
     */
@DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteAvatar(@RequestParam Long userId) {
        log.info("DELETE /api/host/avatar - userId: {}", userId);
        avatarService.removeAvatar(userId);
        log.info("DELETE /api/host/avatar - userId: {} xoá avatar thành công", userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
