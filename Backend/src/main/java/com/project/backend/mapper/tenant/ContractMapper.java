package com.project.backend.mapper.tenant;

import com.project.backend.entity.Contract;
import com.project.backend.entity.ContractService;
import com.project.backend.dto.tenant.contract.ContractServiceDTO;
import com.project.backend.dto.tenant.contract.MyContractDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Vai trò: Mapper của module tenant-service.
 * Chức năng: Chuyển đổi dữ liệu cho nghiệp vụ contract giữa entity và DTO.
 */
@Component("tenantContractMapper")
public class ContractMapper {

        /**
     * Chức năng: Chuyển đổi dto.
     */
public MyContractDTO toDTO(Contract contract, List<ContractService> services) {
        MyContractDTO dto = new MyContractDTO();
        dto.setContractId(contract.getContractId());
        dto.setContractCode(contract.getContractCode());
        dto.setRoomCode(contract.getRoom().getRoomCode());
        dto.setAreaName(contract.getRoom().getArea().getAreaName());
        dto.setAreaAddress(contract.getRoom().getArea().getAddress());
        dto.setStartDate(contract.getStartDate());
        dto.setEndDate(contract.getEndDate());
        dto.setActualRentPrice(contract.getActualRentPrice());
        dto.setStatus(contract.getStatus());
        if (contract.getDeposit() != null) {
            dto.setDepositAmount(contract.getDeposit().getAmount());
            dto.setDepositStatus(contract.getDeposit().getStatus());
            dto.setDepositDate(contract.getDeposit().getDepositDate());
        }

        
        dto.setElecPrice(contract.getElecPriceOverride() != null
                ? contract.getElecPriceOverride()
                : contract.getRoom().getElecPrice());
        dto.setWaterPrice(contract.getWaterPriceOverride() != null
                ? contract.getWaterPriceOverride()
                : contract.getRoom().getWaterPrice());

        
        dto.setContractServices(services.stream()
                .map(this::toContractServiceDTO)
                .toList());

        
        dto.setServiceNames(services.stream()
                .map(cs -> cs.getService().getServiceName())
                .toList());

        long days = LocalDate.now().until(contract.getEndDate()).getDays();
        dto.setDaysUntilExpiry(days);

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
