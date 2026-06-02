package com.project.backend.dto.admin.dashboard;

import com.project.backend.dto.admin.common.AdminAlertDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Vai trò: DTO của module admin-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến admin dashboard để trao đổi giữa các tầng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDTO {
    private Long totalUsers;
    private Long totalHosts;
    private Long totalTenants;
    private Long totalRooms;
    private Long totalContracts;
    private Long occupancyRate;  
    private BigDecimal totalRevenue;
    private BigDecimal thisMonthRevenue;
    private Long overdueInvoices;
    private Long activeContracts;
    private List<AdminAlertDTO> alerts;  
}
