package com.project.backend.controller.host;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.entity.User;
import com.project.backend.dto.host.deposit.DepositCreateDTO;
import com.project.backend.dto.host.deposit.DepositResponseDTO;
import com.project.backend.service.host.DepositService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vai trò: REST controller của module host-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ deposit và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/host/deposits")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

        /**
     * Chức năng: Lấy dữ liệu deposits.
     * URL: GET /api/host/deposits
     */
@GetMapping
    public ResponseEntity<ApiResponse<List<DepositResponseDTO>>> getDeposits(@RequestParam Long hostId) {
        log.info("GET /api/host/deposits - hostId: {}", hostId);
        List<DepositResponseDTO> result = depositService.getDepositsByHost(hostId);
        log.info("GET /api/host/deposits - trả về {} đặt cọc", result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Tạo deposit.
     * URL: POST /api/host/deposits
     */
@PostMapping
    public ResponseEntity<ApiResponse<DepositResponseDTO>> createDeposit(@RequestBody DepositCreateDTO request) {
        log.info("POST /api/host/deposits - tenantId: {}, roomId: {}", request.getTenantId(), request.getRoomId());
        DepositResponseDTO result = depositService.createDeposit(request);
        log.info("POST /api/host/deposits - tạo thành công depositId: {}", result.getDepositId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ confirm deposit.
     * URL: PATCH /api/host/deposits/{depositId}/confirm
     */
@PatchMapping("/{depositId}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmDeposit(@PathVariable Long depositId,
                                                            @RequestParam Long confirmedById) {
        log.info("PATCH /api/host/deposits/{}/confirm - confirmedById: {}", depositId, confirmedById);
        User confirmedBy = new User();
        confirmedBy.setUserId(confirmedById);
        depositService.confirmDeposit(depositId, confirmedBy);
        log.info("PATCH /api/host/deposits/{}/confirm - xác nhận thành công", depositId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ refund deposit.
     * URL: PATCH /api/host/deposits/{depositId}/refund
     */
@PatchMapping("/{depositId}/refund")
    public ResponseEntity<ApiResponse<Void>> refundDeposit(@PathVariable Long depositId) {
        log.info("PATCH /api/host/deposits/{}/refund", depositId);
        depositService.refundDeposit(depositId);
        log.info("PATCH /api/host/deposits/{}/refund - hoàn cọc thành công", depositId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
