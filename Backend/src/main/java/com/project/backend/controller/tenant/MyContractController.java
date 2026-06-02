package com.project.backend.controller.tenant;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.dto.tenant.contract.MyContractDTO;
import com.project.backend.service.tenant.MyContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vai trò: REST controller của module tenant-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ my contract và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/tenant/contracts")
@RequiredArgsConstructor
public class MyContractController {

    private final MyContractService contractService;

        /**
     * Chức năng: Lấy dữ liệu my contracts.
     * URL: GET /api/tenant/contracts
     */
@GetMapping
    public ResponseEntity<ApiResponse<List<MyContractDTO>>> getMyContracts(
            @RequestParam Long userId) {
        log.info("GET /api/tenant/contracts - userId: {}", userId);
        return ResponseEntity.ok(
                ApiResponse.success(contractService.getMyContracts(userId)));
    }

        /**
     * Chức năng: Lấy dữ liệu current contract.
     * URL: GET /api/tenant/contracts/current
     */
@GetMapping("/current")
    public ResponseEntity<ApiResponse<MyContractDTO>> getCurrentContract(
            @RequestParam Long userId) {
        log.info("GET /api/tenant/contracts/current - userId: {}", userId);
        return ResponseEntity.ok(
                ApiResponse.success(contractService.getCurrentContract(userId)));
    }
}
