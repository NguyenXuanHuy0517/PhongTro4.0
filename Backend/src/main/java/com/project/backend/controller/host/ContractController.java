package com.project.backend.controller.host;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.entity.User;
import com.project.backend.dto.host.contract.ContractCreateDTO;
import com.project.backend.dto.host.contract.ContractExtendDTO;
import com.project.backend.dto.host.contract.ContractInviteCreateDTO;
import com.project.backend.dto.host.contract.ContractInviteResponseDTO;
import com.project.backend.dto.host.contract.ContractResponseDTO;
import com.project.backend.service.host.ContractManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/host/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractManagementService contractService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ContractResponseDTO>>> getContracts(@RequestParam Long hostId) {
        log.info("GET /api/host/contracts - hostId: {}", hostId);
        List<ContractResponseDTO> result = contractService.getContractsByHost(hostId);
        log.info("GET /api/host/contracts - tra ve {} hop dong", result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{contractId}")
    public ResponseEntity<ApiResponse<ContractResponseDTO>> getContractDetail(@PathVariable Long contractId) {
        log.info("GET /api/host/contracts/{}", contractId);
        ContractResponseDTO result = contractService.getContractDetail(contractId);
        log.info("GET /api/host/contracts/{} - contractCode: {}", contractId, result.getContractCode());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ContractResponseDTO>> createContract(@RequestBody ContractCreateDTO request) {
        log.info("POST /api/host/contracts - tenantId: {}, roomId: {}", request.getTenantId(), request.getRoomId());
        ContractResponseDTO result = contractService.createContract(request);
        log.info("POST /api/host/contracts - tao thanh cong contractId: {}", result.getContractId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/invitations")
    public ResponseEntity<ApiResponse<ContractInviteResponseDTO>> createContractInvitation(
            @RequestBody ContractInviteCreateDTO request,
            Authentication authentication
    ) {
        log.info("POST /api/host/contracts/invitations - roomId: {}", request.getRoomId());
        ContractInviteResponseDTO result = contractService.createContractInvitation(request, authentication);
        log.info("POST /api/host/contracts/invitations - tao thanh cong ma thue cho phong {}", result.getRoomCode());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/{contractId}/extend")
    public ResponseEntity<ApiResponse<ContractResponseDTO>> extendContract(
            @PathVariable Long contractId,
            @RequestBody ContractExtendDTO request
    ) {
        log.info("PUT /api/host/contracts/{}/extend - newEndDate: {}", contractId, request.getNewEndDate());
        ContractResponseDTO result = contractService.extendContract(contractId, request);
        log.info("PUT /api/host/contracts/{}/extend - gia han thanh cong", contractId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping("/{contractId}/terminate")
    public ResponseEntity<ApiResponse<Void>> terminateContract(
            @PathVariable Long contractId,
            @RequestParam Long terminatedById
    ) {
        log.info("PATCH /api/host/contracts/{}/terminate - terminatedById: {}", contractId, terminatedById);
        User terminatedBy = new User();
        terminatedBy.setUserId(terminatedById);
        contractService.terminateContract(contractId, terminatedBy);
        log.info("PATCH /api/host/contracts/{}/terminate - cham dut thanh cong", contractId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{contractId}/services/{serviceId}")
    public ResponseEntity<ApiResponse<Void>> addService(
            @PathVariable Long contractId,
            @PathVariable Long serviceId
    ) {
        log.info("POST /api/host/contracts/{}/services/{}", contractId, serviceId);
        contractService.addService(contractId, serviceId);
        log.info("POST /api/host/contracts/{}/services/{} - them dich vu thanh cong", contractId, serviceId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{contractId}/services/{serviceId}")
    public ResponseEntity<ApiResponse<Void>> removeService(
            @PathVariable Long contractId,
            @PathVariable Long serviceId
    ) {
        log.info("DELETE /api/host/contracts/{}/services/{}", contractId, serviceId);
        contractService.removeService(contractId, serviceId);
        log.info("DELETE /api/host/contracts/{}/services/{} - xoa dich vu thanh cong", contractId, serviceId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
