package com.project.backend.dto.host.equipment;

import lombok.Data;
import java.time.LocalDate;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến room asset create để trao đổi giữa các tầng.
 */
@Data
public class RoomAssetCreateDTO {
    private Long roomId;
    private Long equipmentId;
    private LocalDate assignedDate;
    private String note;
}
