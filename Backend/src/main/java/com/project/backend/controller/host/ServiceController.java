package com.project.backend.controller.host;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.dto.host.service.ServiceCreateDTO;
import com.project.backend.dto.host.service.ServiceResponseDTO;
import com.project.backend.service.host.ServiceManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vai trò: REST controller của module host-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ service và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/host")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceManagementService serviceManagementService;

        /**
     * Chức năng: Lấy dữ liệu services.
     * URL: GET /api/host/areas/{areaId}/services
     */
@GetMapping("/areas/{areaId}/services")
    public ResponseEntity<ApiResponse<List<ServiceResponseDTO>>> getServices(@PathVariable Long areaId) {
        log.info("GET /api/host/areas/{}/services", areaId);
        List<ServiceResponseDTO> result = serviceManagementService.getServicesByArea(areaId);
        log.info("GET /api/host/areas/{}/services - trả về {} dịch vụ", areaId, result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Tạo service.
     * URL: POST /api/host/areas/{areaId}/services
     */
@PostMapping("/areas/{areaId}/services")
    public ResponseEntity<ApiResponse<ServiceResponseDTO>> createService(@PathVariable Long areaId,
                                                                         @RequestBody ServiceCreateDTO request) {
        log.info("POST /api/host/areas/{}/services - serviceName: {}", areaId, request.getServiceName());
        ServiceResponseDTO result = serviceManagementService.createService(areaId, request);
        log.info("POST /api/host/areas/{}/services - tạo thành công serviceId: {}", areaId, result.getServiceId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Cập nhật service.
     * URL: PUT /api/host/services/{serviceId}
     */
@PutMapping("/services/{serviceId}")
    public ResponseEntity<ApiResponse<ServiceResponseDTO>> updateService(@PathVariable Long serviceId,
                                                                         @RequestBody ServiceCreateDTO request) {
        log.info("PUT /api/host/services/{}", serviceId);
        ServiceResponseDTO result = serviceManagementService.updateService(serviceId, request);
        log.info("PUT /api/host/services/{} - cập nhật thành công", serviceId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Xóa service.
     * URL: DELETE /api/host/services/{serviceId}
     */
@DeleteMapping("/services/{serviceId}")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Long serviceId) {
        log.info("DELETE /api/host/services/{}", serviceId);
        serviceManagementService.deleteService(serviceId);
        log.info("DELETE /api/host/services/{} - xóa thành công", serviceId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
