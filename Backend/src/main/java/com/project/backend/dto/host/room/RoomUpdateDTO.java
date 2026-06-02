package com.project.backend.dto.host.room;

import lombok.Data;
import java.math.BigDecimal;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến room update để trao đổi giữa các tầng.
 */
@Data
public class RoomUpdateDTO {
    private BigDecimal basePrice;
    private BigDecimal elecPrice;
    private BigDecimal waterPrice;
    private BigDecimal areaSize;
    private String amenities;
    private String images;
    private String description;
}
