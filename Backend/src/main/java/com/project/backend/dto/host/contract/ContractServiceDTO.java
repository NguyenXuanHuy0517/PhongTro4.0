package com.project.backend.dto.host.contract;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến contract service để trao đổi giữa các tầng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractServiceDTO {
    private Long contractServiceId;    
    private Long serviceId;
    private String serviceName;
    private Integer quantity;
    private BigDecimal price;          
    private String unitName;

    
    private BigDecimal currentServicePrice;  
}
