package com.project.backend.dto.host.room;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến room để trao đổi giữa các tầng.
 */
@Data
public class RoomResponseDTO {
    private Long roomId;
    private String roomCode;
    private Integer floor;
    private BigDecimal basePrice;
    private BigDecimal elecPrice;
    private BigDecimal waterPrice;
    private BigDecimal areaSize;
    private String status;
    private String amenities;
    private String images;  
    private List<String> imagesList;  
    private String description;
    private Long areaId;
    private String areaName;
    private String hostName;
    private String hostAvatar;  
    private String currentTenantName;
    private Long currentContractId;
}
