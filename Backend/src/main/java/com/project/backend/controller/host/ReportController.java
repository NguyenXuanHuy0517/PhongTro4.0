package com.project.backend.controller.host;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.dto.host.report.ReportDTO;
import com.project.backend.service.host.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Vai trò: REST controller của module host-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ report và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/host/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

        /**
     * Chức năng: Lấy dữ liệu dashboard.
     * URL: GET /api/host/reports/dashboard
     */
@GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<ReportDTO>> getDashboard(@RequestParam Long hostId) {
        log.info("GET /api/host/reports/dashboard - hostId: {}", hostId);
        ReportDTO result = reportService.getDashboard(hostId);
        log.info("GET /api/host/reports/dashboard - revenue: {}, occupancy: {}%",
                result.getTotalRevenue(), result.getOccupancyRate());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
