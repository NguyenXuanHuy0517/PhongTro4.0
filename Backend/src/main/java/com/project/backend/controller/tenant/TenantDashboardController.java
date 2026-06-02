package com.project.backend.controller.tenant;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.dto.tenant.dashboard.TenantDashboardSummaryDTO;
import com.project.backend.service.tenant.TenantDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/tenant/dashboard")
@RequiredArgsConstructor
public class TenantDashboardController {

    private final TenantDashboardService tenantDashboardService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<TenantDashboardSummaryDTO>> getSummary(@RequestParam Long userId) {
        log.info("GET /api/tenant/dashboard/summary - userId: {}", userId);
        return ResponseEntity.ok(ApiResponse.success(tenantDashboardService.getSummary(userId)));
    }
}
