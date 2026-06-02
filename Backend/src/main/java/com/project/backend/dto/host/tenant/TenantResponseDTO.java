package com.project.backend.dto.host.tenant;

import lombok.Data;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến tenant để trao đổi giữa các tầng.
 */
@Data
public class TenantResponseDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String idCardNumber;
    private boolean active;
    private String currentRoomCode;
    private String contractStatus;
}
