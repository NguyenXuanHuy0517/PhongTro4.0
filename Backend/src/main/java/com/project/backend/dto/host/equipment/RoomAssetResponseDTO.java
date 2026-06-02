package com.project.backend.dto.host.equipment;

import lombok.Data;
import java.time.LocalDate;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến room asset để trao đổi giữa các tầng.
 */
@Data
public class RoomAssetResponseDTO {
    private Long equipmentId;
    private String equipmentName;
    private String serialNumber;
    private String status;
    private LocalDate assignedDate;
    private String note;
}
