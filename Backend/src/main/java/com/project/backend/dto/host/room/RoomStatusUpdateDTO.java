package com.project.backend.dto.host.room;

import lombok.Data;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến room status update để trao đổi giữa các tầng.
 */
@Data
public class RoomStatusUpdateDTO {
    private String status;
    private String note;
}
