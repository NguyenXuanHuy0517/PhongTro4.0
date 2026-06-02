package com.project.backend.service.host;

import com.project.backend.entity.Contract;
import com.project.backend.entity.ContractService;
import com.project.backend.entity.ContractServiceId;
import com.project.backend.entity.Room;
import com.project.backend.entity.User;
import com.project.backend.repository.ContractRepository;
import com.project.backend.repository.ContractServiceRepository;
import com.project.backend.repository.DepositRepository;
import com.project.backend.repository.RoomRepository;
import com.project.backend.repository.ServiceRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.dto.host.contract.ContractCreateDTO;
import com.project.backend.dto.host.contract.ContractExtendDTO;
import com.project.backend.dto.host.contract.ContractInviteCreateDTO;
import com.project.backend.dto.host.contract.ContractInviteResponseDTO;
import com.project.backend.dto.host.contract.ContractResponseDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.mapper.host.ContractMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractManagementService {

    private final ContractRepository contractRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final DepositRepository depositRepository;
    private final ServiceRepository serviceRepository;
    private final ContractServiceRepository contractServiceRepository;
    private final ContractMapper contractMapper;
    private final ContractInvitationTokenService contractInvitationTokenService;

    public List<ContractResponseDTO> getContractsByHost(Long hostId) {
        List<Contract> contracts = contractRepository.findWithRelationsByHostId(hostId);
        Map<Long, List<ContractService>> servicesByContractId = mapContractServicesByContractId(contracts);

        return contracts.stream()
                .map(contract -> contractMapper.toDTO(
                        contract,
                        servicesByContractId.getOrDefault(contract.getContractId(), Collections.emptyList())
                ))
                .toList();
    }

    public ContractResponseDTO getContractDetail(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay hop dong: " + contractId));
        return contractMapper.toDTO(
                contract,
                contractServiceRepository.findByContract_ContractId(contractId)
        );
    }

    @Transactional(readOnly = true)
    public ContractInviteResponseDTO createContractInvitation(
            ContractInviteCreateDTO request,
            Authentication authentication
    ) {
        User host = requireAuthenticatedHost(authentication);
        validateInvitationRequest(request);

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay phong: " + request.getRoomId()));

        if (!room.getArea().getHost().getUserId().equals(host.getUserId())) {
            throw new SecurityException("Ban khong co quyen tao ma thue cho phong nay");
        }

        if (!"AVAILABLE".equals(room.getStatus())) {
            throw new IllegalStateException(
                    "Phong " + room.getRoomCode() + " hien dang o trang thai " + room.getStatus()
                            + " va khong the tao ma thue."
            );
        }

        ContractInvitationTokenService.IssuedInvitation issuedInvitation =
                contractInvitationTokenService.issueInvitation(host, room, request);

        ContractInviteResponseDTO response = new ContractInviteResponseDTO();
        response.setInviteCode(issuedInvitation.inviteCode());
        response.setExpiresAt(issuedInvitation.expiresAt());
        response.setRoomCode(room.getRoomCode());
        response.setAreaName(room.getArea().getAreaName());
        response.setAreaAddress(room.getArea().getAddress());
        response.setStartDate(request.getStartDate());
        response.setEndDate(request.getEndDate());
        response.setActualRentPrice(request.getActualRentPrice());
        response.setElecPriceOverride(request.getElecPriceOverride());
        response.setWaterPriceOverride(request.getWaterPriceOverride());
        response.setPenaltyTerms(normalizeText(request.getPenaltyTerms()));
        return response;
    }

    @Transactional
    public ContractResponseDTO createContract(ContractCreateDTO request) {
        User tenant = userRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay nguoi thue: " + request.getTenantId()));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay phong: " + request.getRoomId()));

        if (!"AVAILABLE".equals(room.getStatus()) && !"DEPOSITED".equals(room.getStatus())) {
            throw new IllegalStateException(
                    "Phong " + room.getRoomCode() + " hien dang o trang thai " + room.getStatus()
                            + " va khong the tao hop dong."
            );
        }

        Contract contract = new Contract();
        contract.setRoom(room);
        contract.setTenant(tenant);
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setActualRentPrice(request.getActualRentPrice());
        contract.setElecPriceOverride(request.getElecPriceOverride());
        contract.setWaterPriceOverride(request.getWaterPriceOverride());
        contract.setPenaltyTerms(normalizeText(request.getPenaltyTerms()));
        contract.setStatus("ACTIVE");
        contract.setContractCode("HD-" + request.getRoomId() + "-" + System.currentTimeMillis());

        if (request.getDepositId() != null) {
            depositRepository.findById(request.getDepositId()).ifPresent(deposit -> {
                contract.setDeposit(deposit);
                deposit.setStatus("COMPLETED");
                depositRepository.save(deposit);
            });
        }

        room.setStatus("RENTED");
        roomRepository.save(room);
        contractRepository.save(contract);

        return contractMapper.toDTO(
                contract,
                contractServiceRepository.findByContract_ContractId(contract.getContractId())
        );
    }

    public ContractResponseDTO extendContract(Long contractId, ContractExtendDTO request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay hop dong: " + contractId));
        contract.setEndDate(request.getNewEndDate());
        contractRepository.save(contract);
        return contractMapper.toDTO(
                contract,
                contractServiceRepository.findByContract_ContractId(contractId)
        );
    }

    @Transactional
    public void terminateContract(Long contractId, User terminatedBy) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay hop dong: " + contractId));
        contract.setStatus("TERMINATED_EARLY");
        contract.setTerminatedAt(LocalDateTime.now());
        contract.setTerminatedBy(terminatedBy);

        Room room = contract.getRoom();
        room.setStatus("AVAILABLE");
        roomRepository.save(room);
        contractRepository.save(contract);
    }

    public void addService(Long contractId, Long serviceId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay hop dong: " + contractId));

        com.project.backend.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay dich vu: " + serviceId));

        ContractServiceId id = new ContractServiceId();
        id.setContractId(contractId);
        id.setServiceId(serviceId);

        ContractService contractService = new ContractService();
        contractService.setId(id);
        contractService.setContract(contract);
        contractService.setService(service);
        contractService.setPriceSnapshot(service.getPrice());
        contractService.setQuantity(1);

        contractServiceRepository.save(contractService);
    }

    public void removeService(Long contractId, Long serviceId) {
        contractServiceRepository.deleteByContract_ContractIdAndService_ServiceId(contractId, serviceId);
    }

    private User requireAuthenticatedHost(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Vui long dang nhap de tao ma thue");
        }

        User host = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Khong tim thay tai khoan: " + authentication.getName()
                ));

        if (host.getRole() == null || !"HOST".equalsIgnoreCase(host.getRole().getRoleName())) {
            throw new SecurityException("Chi chu tro moi duoc tao ma thue");
        }

        return host;
    }

    private void validateInvitationRequest(ContractInviteCreateDTO request) {
        if (request.getRoomId() == null) {
            throw new IllegalArgumentException("Phong khong duoc de trong");
        }
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("Ngay bat dau va ngay ket thuc la bat buoc");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("Ngay ket thuc phai sau hoac bang ngay bat dau");
        }
        if (request.getActualRentPrice() == null || request.getActualRentPrice().signum() <= 0) {
            throw new IllegalArgumentException("Gia thue phai lon hon 0");
        }
        if (request.getElecPriceOverride() != null && request.getElecPriceOverride().signum() < 0) {
            throw new IllegalArgumentException("Gia dien khong hop le");
        }
        if (request.getWaterPriceOverride() != null && request.getWaterPriceOverride().signum() < 0) {
            throw new IllegalArgumentException("Gia nuoc khong hop le");
        }
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Map<Long, List<ContractService>> mapContractServicesByContractId(List<Contract> contracts) {
        List<Long> contractIds = contracts.stream()
                .map(Contract::getContractId)
                .toList();

        if (contractIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return contractServiceRepository.findByContract_ContractIdIn(contractIds).stream()
                .collect(Collectors.groupingBy(contractService -> contractService.getContract().getContractId()));
    }
}
