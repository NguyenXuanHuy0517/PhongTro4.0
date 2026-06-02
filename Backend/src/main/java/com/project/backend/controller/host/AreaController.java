package com.project.backend.controller.host;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.dto.host.area.AreaCreateDTO;
import com.project.backend.dto.host.area.AreaResponseDTO;
import com.project.backend.service.host.AreaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vai trò: REST controller của module host-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ area và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/host/areas")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

        /**
     * Chức năng: Lấy dữ liệu areas.
     * URL: GET /api/host/areas
     */
@GetMapping
    public ResponseEntity<ApiResponse<List<AreaResponseDTO>>> getAreas(@RequestParam Long hostId) {
        log.info("GET /api/host/areas - hostId: {}", hostId);
        List<AreaResponseDTO> result = areaService.getAreasByHost(hostId);
        log.info("GET /api/host/areas - trả về {} khu trọ", result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Tạo area.
     * URL: POST /api/host/areas
     */
@PostMapping
    public ResponseEntity<ApiResponse<AreaResponseDTO>> createArea(@RequestParam Long hostId,
                                                                   @RequestBody AreaCreateDTO request) {
        log.info("POST /api/host/areas - hostId: {}, areaName: {}", hostId, request.getAreaName());
        AreaResponseDTO result = areaService.createArea(hostId, request);
        log.info("POST /api/host/areas - tạo thành công areaId: {}", result.getAreaId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Cập nhật area.
     * URL: PUT /api/host/areas/{areaId}
     */
@PutMapping("/{areaId}")
    public ResponseEntity<ApiResponse<AreaResponseDTO>> updateArea(@PathVariable Long areaId,
                                                                   @RequestBody AreaCreateDTO request) {
        log.info("PUT /api/host/areas/{} - areaName: {}", areaId, request.getAreaName());
        AreaResponseDTO result = areaService.updateArea(areaId, request);
        log.info("PUT /api/host/areas/{} - cập nhật thành công", areaId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
