package com.project.backend.mapper.host;

import com.project.backend.entity.Service;
import com.project.backend.dto.host.service.ServiceResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Vai trò: Mapper của module host-service.
 * Chức năng: Chuyển đổi dữ liệu cho nghiệp vụ service giữa entity và DTO.
 */
@Component
public class ServiceMapper {

        /**
     * Chức năng: Chuyển đổi dto.
     */
public ServiceResponseDTO toDTO(Service service, int usageCount) {
        ServiceResponseDTO dto = new ServiceResponseDTO();
        dto.setServiceId(service.getServiceId());
        dto.setServiceName(service.getServiceName());
        dto.setPrice(service.getPrice());
        dto.setUnitName(service.getUnitName());
        dto.setDescription(service.getDescription());
        dto.setActive(service.isActive());
        dto.setUsageCount(usageCount);
        return dto;
    }
}
