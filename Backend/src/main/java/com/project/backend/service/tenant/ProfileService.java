package com.project.backend.service.tenant;

import com.project.backend.entity.User;
import com.project.backend.repository.UserRepository;
import com.project.backend.dto.tenant.profile.ProfileResponseDTO;
import com.project.backend.dto.tenant.profile.ProfileUpdateDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.mapper.tenant.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Vai trò: Service xử lý nghiệp vụ của module tenant-service.
 * Chức năng: Chứa logic xử lý liên quan đến profile.
 */
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;

        /**
     * Chức năng: Lấy dữ liệu profile.
     */
public ProfileResponseDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + userId));
        return profileMapper.toDTO(user);
    }

        /**
     * Chức năng: Cập nhật profile.
     */
public ProfileResponseDTO updateProfile(Long userId, ProfileUpdateDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + userId));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getFcmToken() != null) {
            user.setFcmToken(request.getFcmToken());
        }

        userRepository.save(user);
        return profileMapper.toDTO(user);
    }
}
