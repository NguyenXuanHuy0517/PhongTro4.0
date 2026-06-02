package com.project.backend.mapper.host;

import com.project.backend.entity.User;
import com.project.backend.dto.host.tenant.TenantResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Vai trò: Mapper của module host-service.
 * Chức năng: Chuyển đổi dữ liệu cho nghiệp vụ tenant giữa entity và DTO.
 */
@Component
public class TenantMapper {

        /**
     * Chức năng: Chuyển đổi dto.
     */
public TenantResponseDTO toDTO(User user) {
        TenantResponseDTO dto = new TenantResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setIdCardNumber(user.getIdCardNumber());
        dto.setActive(user.isActive());
        return dto;
    }
}
