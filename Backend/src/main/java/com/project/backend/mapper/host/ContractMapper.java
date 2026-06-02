package com.project.backend.mapper.host;

import com.project.backend.entity.Contract;
import com.project.backend.entity.ContractService;
import com.project.backend.dto.host.contract.ContractResponseDTO;
import com.project.backend.dto.host.contract.ContractServiceDTO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Vai trò: Mapper của module host-service.
 * Chức năng: Chuyển đổi dữ liệu cho nghiệp vụ contract giữa entity và DTO.
 */
@Component("hostContractMapper")
public class ContractMapper {

        /**
     * Chức năng: Chuyển đổi dto.
     */
public ContractResponseDTO toDTO(Contract contract, List<ContractService> services) {
        ContractResponseDTO dto = new ContractResponseDTO();
        dto.setContractId(contract.getContractId());
        dto.setContractCode(contract.getContractCode());
        dto.setTenantName(contract.getTenant().getFullName());
        dto.setRoomCode(contract.getRoom().getRoomCode());
        dto.setAreaName(contract.getRoom().getArea().getAreaName());
        dto.setStartDate(contract.getStartDate());
        dto.setEndDate(contract.getEndDate());
        dto.setActualRentPrice(contract.getActualRentPrice());
        dto.setElecPriceOverride(contract.getElecPriceOverride());
        dto.setWaterPriceOverride(contract.getWaterPriceOverride());
        dto.setStatus(contract.getStatus());
        if (contract.getDeposit() != null) {
            dto.setDepositAmount(contract.getDeposit().getAmount());
            dto.setDepositStatus(contract.getDeposit().getStatus());
            dto.setDepositDate(contract.getDeposit().getDepositDate());
        }
        
        
        dto.setContractServices(services.stream()
                .map(this::toContractServiceDTO)
                .toList());
        
        
        dto.setServiceNames(services.stream()
                .map(cs -> cs.getService().getServiceName())
                .toList());
        return dto;
    }

    
        /**
     * Chức năng: Chuyển đổi contract service dto.
     */
private ContractServiceDTO toContractServiceDTO(ContractService contractService) {
        ContractServiceDTO dto = new ContractServiceDTO();
        dto.setContractServiceId(contractService.getId().getServiceId());
        dto.setServiceId(contractService.getService().getServiceId());
        dto.setServiceName(contractService.getService().getServiceName());
        dto.setQuantity(contractService.getQuantity());
        dto.setPrice(contractService.getPriceSnapshot());
        dto.setUnitName(contractService.getService().getUnitName());
        dto.setCurrentServicePrice(contractService.getService().getPrice());
        return dto;
    }
}
