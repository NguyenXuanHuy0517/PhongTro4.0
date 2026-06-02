package com.project.backend.service.tenant;

import com.project.backend.entity.Contract;
import com.project.backend.entity.Room;
import com.project.backend.entity.User;
import com.project.backend.repository.ContractRepository;
import com.project.backend.repository.ContractServiceRepository;
import com.project.backend.repository.RoomRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.dto.tenant.contract.MyContractDTO;
import com.project.backend.dto.tenant.contract.RentalJoinPreviewDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.mapper.tenant.ContractMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalJoinService {

    private final ContractRepository contractRepository;
    private final ContractServiceRepository contractServiceRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ContractMapper contractMapper;
    private final TenantContractInvitationTokenService contractInvitationTokenService;

    @Transactional(readOnly = true)
    public RentalJoinPreviewDTO previewInvitation(String inviteCode, Authentication authentication) {
        User tenant = requireAuthenticatedTenant(authentication);
        ensureTenantHasNoActiveContract(tenant);

        TenantContractInvitationTokenService.ContractInvitationPayload payload =
                contractInvitationTokenService.parseInvitation(inviteCode);
        Room room = getClaimableRoom(payload, false);

        RentalJoinPreviewDTO preview = new RentalJoinPreviewDTO();
        preview.setRoomCode(room.getRoomCode());
        preview.setAreaName(room.getArea().getAreaName());
        preview.setAreaAddress(room.getArea().getAddress());
        preview.setStartDate(payload.startDate());
        preview.setEndDate(payload.endDate());
        preview.setActualRentPrice(payload.actualRentPrice());
        preview.setElecPrice(payload.elecPriceOverride() != null
                ? payload.elecPriceOverride()
                : room.getElecPrice());
        preview.setWaterPrice(payload.waterPriceOverride() != null
                ? payload.waterPriceOverride()
                : room.getWaterPrice());
        preview.setPenaltyTerms(payload.penaltyTerms());
        preview.setExpiresAt(payload.expiresAt());
        return preview;
    }

    @Transactional
    public MyContractDTO claimInvitation(String inviteCode, Authentication authentication) {
        User tenant = requireAuthenticatedTenant(authentication);
        ensureTenantHasNoActiveContract(tenant);

        TenantContractInvitationTokenService.ContractInvitationPayload payload =
                contractInvitationTokenService.parseInvitation(inviteCode);
        Room room = getClaimableRoom(payload, true);

        Contract contract = new Contract();
        contract.setContractCode("HD-" + room.getRoomId() + "-" + System.currentTimeMillis());
        contract.setRoom(room);
        contract.setTenant(tenant);
        contract.setStartDate(payload.startDate());
        contract.setEndDate(payload.endDate());
        contract.setActualRentPrice(payload.actualRentPrice());
        contract.setElecPriceOverride(payload.elecPriceOverride());
        contract.setWaterPriceOverride(payload.waterPriceOverride());
        contract.setPenaltyTerms(normalizeText(payload.penaltyTerms()));
        contract.setStatus("ACTIVE");

        room.setStatus("RENTED");
        roomRepository.save(room);
        contractRepository.save(contract);

        return contractMapper.toDTO(
                contract,
                contractServiceRepository.findByContract_ContractId(contract.getContractId())
        );
    }

    private User requireAuthenticatedTenant(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Vui long dang nhap de nhan phong");
        }

        User tenant = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Khong tim thay tai khoan: " + authentication.getName()
                ));

        if (tenant.getRole() == null || !"TENANT".equalsIgnoreCase(tenant.getRole().getRoleName())) {
            throw new SecurityException("Chi nguoi thue moi duoc nhap ma thue");
        }

        return tenant;
    }

    private void ensureTenantHasNoActiveContract(User tenant) {
        if (contractRepository.findFirstByTenant_UserIdAndStatusOrderByStartDateDesc(
                tenant.getUserId(),
                "ACTIVE"
        ).isPresent()) {
            throw new IllegalStateException("Tai khoan nay da co hop dong dang hieu luc");
        }
    }

    private Room getClaimableRoom(
            TenantContractInvitationTokenService.ContractInvitationPayload payload,
            boolean forClaim
    ) {
        Room room = (forClaim
                ? roomRepository.findByIdForUpdate(payload.roomId())
                : roomRepository.findById(payload.roomId()))
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay phong: " + payload.roomId()));

        if (!room.getArea().getHost().getUserId().equals(payload.hostId())) {
            throw new IllegalArgumentException("Ma thue khong con hop le");
        }

        if (!"AVAILABLE".equals(room.getStatus())) {
            throw new IllegalStateException("Phong nay khong con trong");
        }

        if (contractRepository.findByRoom_RoomIdAndStatus(room.getRoomId(), "ACTIVE").isPresent()) {
            throw new IllegalStateException("Phong nay da duoc tao hop dong boi nguoi khac");
        }

        return room;
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
