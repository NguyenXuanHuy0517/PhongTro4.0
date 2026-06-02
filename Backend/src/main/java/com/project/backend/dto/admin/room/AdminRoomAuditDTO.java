package com.project.backend.dto.admin.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Vai trò: DTO của module admin-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến admin room audit để trao đổi giữa các tầng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRoomAuditDTO {
    private Long roomId;
    private String roomCode;
    private String areaName;
    private String hostName;
    private String status;
    private BigDecimal basePrice;
    private String currentTenantName;  
    private Long daysWithoutInvoice;   
}
