package com.project.backend.service.host;

import com.project.backend.entity.Role;
import com.project.backend.entity.User;
import com.project.backend.repository.ContractRepository;
import com.project.backend.repository.RoleRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.dto.host.tenant.TenantCreateDTO;
import com.project.backend.dto.host.tenant.TenantResponseDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.mapper.host.TenantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Vai trò: Service xử lý nghiệp vụ của module host-service.
 * Chức năng: Chứa logic xử lý liên quan đến tenant.
 */
@Service
@RequiredArgsConstructor
public class TenantService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ContractRepository contractRepository;
    private final PasswordEncoder passwordEncoder;
    private final TenantMapper tenantMapper;

    

        /**
     * Chức năng: Lấy dữ liệu tenants by host.
     */
public List<TenantResponseDTO> getTenantsByHost(Long hostId) {
        return contractRepository.findByRoom_Area_Host_UserId(hostId).stream()
                .map(contract -> contract.getTenant())
                .distinct()
                .map(user -> {
                    TenantResponseDTO dto = tenantMapper.toDTO(user);

                    
                    contractRepository.findByTenant_UserId(user.getUserId()).stream()
                            .filter(c -> "ACTIVE".equals(c.getStatus()))
                            .filter(c -> c.getRoom().getArea().getHost()
                                    .getUserId().equals(hostId))
                            .findFirst()
                            .ifPresent(c -> {
                                dto.setCurrentRoomCode(c.getRoom().getRoomCode());
                                dto.setContractStatus(c.getStatus());
                            });

                    return dto;
                })
                .toList();
    }

        /**
     * Chức năng: Lấy dữ liệu tenant detail.
     */
public TenantResponseDTO getTenantDetail(Long tenantId) {
        User user = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người thuê: " + tenantId));

        TenantResponseDTO dto = tenantMapper.toDTO(user);

        contractRepository.findByTenant_UserId(tenantId).stream()
                .filter(c -> "ACTIVE".equals(c.getStatus()))
                .findFirst()
                .ifPresent(contract -> {
                    dto.setCurrentRoomCode(contract.getRoom().getRoomCode());
                    dto.setContractStatus(contract.getStatus());
                });

        return dto;
    }

        /**
     * Chức năng: Tạo tenant.
     */
public TenantResponseDTO createTenant(TenantCreateDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại: " + request.getEmail());
        }
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại: " + request.getPhoneNumber());
        }

        Role role = roleRepository.findByRoleName("TENANT")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy role TENANT"));

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setIdCardNumber(request.getIdCardNumber());
        user.setRole(role);
        user.setActive(true);

        userRepository.save(user);
        return tenantMapper.toDTO(user);
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ toggle active.
     */
public void toggleActive(Long tenantId) {
        User user = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người thuê: " + tenantId));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }
}
