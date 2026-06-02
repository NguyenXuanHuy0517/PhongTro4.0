package com.project.backend.dto.host.report;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến report để trao đổi giữa các tầng.
 */
@Data
public class ReportDTO {
    private BigDecimal totalRevenue;
    private BigDecimal previousRevenue;
    private int totalRooms;
    private int rentedRooms;
    private int availableRooms;
    private int maintenanceRooms;
    private double occupancyRate;
    private int overdueCount;
    private int openIssueCount;
    private List<String> topServices;
}
