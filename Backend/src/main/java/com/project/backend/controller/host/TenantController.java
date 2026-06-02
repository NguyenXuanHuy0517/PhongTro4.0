package com.project.backend.controller.host;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.dto.host.tenant.TenantCreateDTO;
import com.project.backend.dto.host.tenant.TenantResponseDTO;
import com.project.backend.service.host.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vai trò: REST controller của module host-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ tenant và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/host/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

        /**
     * Chức năng: Lấy dữ liệu tenants.
     * URL: GET /api/host/tenants
     */
@GetMapping
    public ResponseEntity<ApiResponse<List<TenantResponseDTO>>> getTenants(@RequestParam Long hostId) {
        log.info("GET /api/host/tenants - hostId: {}", hostId);
        List<TenantResponseDTO> result = tenantService.getTenantsByHost(hostId);
        log.info("GET /api/host/tenants - trả về {} người thuê", result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Lấy dữ liệu tenant detail.
     * URL: GET /api/host/tenants/{tenantId}
     */
@GetMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<TenantResponseDTO>> getTenantDetail(@PathVariable Long tenantId) {
        log.info("GET /api/host/tenants/{}", tenantId);
        TenantResponseDTO result = tenantService.getTenantDetail(tenantId);
        log.info("GET /api/host/tenants/{} - fullName: {}", tenantId, result.getFullName());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Tạo tenant.
     * URL: POST /api/host/tenants
     */
@PostMapping
    public ResponseEntity<ApiResponse<TenantResponseDTO>> createTenant(@RequestBody TenantCreateDTO request) {
        log.info("POST /api/host/tenants - email: {}", request.getEmail());
        TenantResponseDTO result = tenantService.createTenant(request);
        log.info("POST /api/host/tenants - tạo thành công userId: {}", result.getUserId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ toggle active.
     * URL: PATCH /api/host/tenants/{tenantId}/toggle
     */
@PatchMapping("/{tenantId}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleActive(@PathVariable Long tenantId) {
        log.info("PATCH /api/host/tenants/{}/toggle", tenantId);
        tenantService.toggleActive(tenantId);
        log.info("PATCH /api/host/tenants/{}/toggle - thành công", tenantId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
