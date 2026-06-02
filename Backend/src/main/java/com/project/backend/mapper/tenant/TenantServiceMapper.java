package com.project.backend.mapper.tenant;

import com.project.backend.entity.Service;
import com.project.backend.entity.ContractService;
import com.project.backend.dto.tenant.service.TenantServiceDTO;
import org.springframework.stereotype.Component;

/**
 * Vai trò: Mapper của module tenant-service.
 * Chức năng: Chuyển đổi dữ liệu cho nghiệp vụ tenant service giữa entity và DTO.
 */
@Component
public class TenantServiceMapper {

        /**
     * Chức năng: Chuyển đổi dto.
     */
public TenantServiceDTO toDTO(Service service) {
        if (service == null) {
            return null;
        }

        return new TenantServiceDTO(
                service.getServiceId(),
                service.getServiceName(),
                0,  
                service.getPrice(),
                service.getUnitName(),
                service.getDescription(),
                null,  
                null,  
                null   
        );
    }

    
        /**
     * Chức năng: Chuyển đổi dto from contract service.
     */
public TenantServiceDTO toDTOFromContractService(ContractService contractService) {
        if (contractService == null || contractService.getService() == null) {
            return null;
        }

        Service service = contractService.getService();
        return new TenantServiceDTO(
                service.getServiceId(),
                service.getServiceName(),
                contractService.getQuantity(),
                service.getPrice(),
                service.getUnitName(),
                service.getDescription(),
                contractService.getId().getServiceId(),  
                contractService.getPriceSnapshot(),      
                service.getUnitName()                    
        );
    }
}
