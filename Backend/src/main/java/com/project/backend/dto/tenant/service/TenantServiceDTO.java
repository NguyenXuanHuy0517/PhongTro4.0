package com.project.backend.dto.tenant.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Vai trò: Service xử lý nghiệp vụ của module tenant-service.
 * Chức năng: Chứa logic xử lý liên quan đến tenant service dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantServiceDTO {
    private Long serviceId;
    private String serviceName;
    private Integer quantity;           
    private BigDecimal price;
    private String unitName;
    private String description;

    
    private Long contractServiceId;     
    private BigDecimal priceSnapshot;   
    private String unitSnapshot;        
}
