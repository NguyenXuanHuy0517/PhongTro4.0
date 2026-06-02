package com.project.backend.mapper.tenant;

import com.project.backend.entity.User;
import com.project.backend.dto.tenant.profile.ProfileResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Vai trò: Mapper của module tenant-service.
 * Chức năng: Chuyển đổi dữ liệu cho nghiệp vụ profile giữa entity và DTO.
 */
@Component
public class ProfileMapper {

        /**
     * Chức năng: Chuyển đổi dto.
     */
public ProfileResponseDTO toDTO(User user) {
        ProfileResponseDTO dto = new ProfileResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setIdCardNumber(user.getIdCardNumber());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setRole(user.getRole().getRoleName());
        return dto;
    }
}
