package com.project.backend.dto.admin.revenue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Vai trò: DTO của module admin-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến admin revenue để trao đổi giữa các tầng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRevenueDTO {
    private BigDecimal totalRevenue;
    private BigDecimal averageRevenue;
    private Map<String, BigDecimal> revenueByPeriod;  
}
