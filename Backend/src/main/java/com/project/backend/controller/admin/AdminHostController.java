package com.project.backend.controller.admin;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.dto.admin.host.AdminHostDetailDTO;
import com.project.backend.dto.admin.host.AdminHostResponseDTO;
import com.project.backend.dto.admin.host.AdminHostStatusUpdateRequest;
import com.project.backend.service.admin.AdminHostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vai trò: REST controller của module admin-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ admin host và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/hosts")
@RequiredArgsConstructor
public class AdminHostController {

    private final AdminHostService hostService;

        /**
     * Chức năng: Lấy dữ liệu all hosts.
     * URL: GET /api/admin/hosts
     */
@GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AdminHostResponseDTO>>> getAllHosts() {
        log.info("GET /api/admin/hosts");
        List<AdminHostResponseDTO> hosts = hostService.getAllHosts();
        log.info("GET /api/admin/hosts - trả về {} host", hosts.size());
        return ResponseEntity.ok(ApiResponse.success(hosts));
    }

        /**
     * Chức năng: Lấy dữ liệu host detail.
     * URL: GET /api/admin/hosts/{hostId}
     */
@GetMapping("/{hostId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminHostDetailDTO>> getHostDetail(@PathVariable Long hostId) {
        log.info("GET /api/admin/hosts/{}", hostId);
        AdminHostDetailDTO host = hostService.getHostDetail(hostId);
        log.info("GET /api/admin/hosts/{} - trả về chi tiết host", hostId);
        return ResponseEntity.ok(ApiResponse.success(host));
    }

        /**
     * Chức năng: Cập nhật host status.
     * URL: PATCH /api/admin/hosts/{hostId}/status
     */
@PatchMapping("/{hostId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateHostStatus(
            @PathVariable Long hostId,
            @RequestBody AdminHostStatusUpdateRequest request) {
        log.info("PATCH /api/admin/hosts/{}/status - active: {}, reason: {}", hostId, request.isActive(), request.getReason());
        hostService.updateHostStatus(hostId, request);
        log.info("PATCH /api/admin/hosts/{}/status - thành công", hostId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
