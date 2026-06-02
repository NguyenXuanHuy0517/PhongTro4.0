package com.project.backend.dto.host.equipment;

import lombok.Data;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến equipment để trao đổi giữa các tầng.
 */
@Data
public class EquipmentDTO {
    private Long equipmentId;
    private String name;
    private String serialNumber;
    private String status;
    private String note;
}
