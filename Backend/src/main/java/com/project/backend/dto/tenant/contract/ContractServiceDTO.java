package com.project.backend.dto.tenant.contract;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
