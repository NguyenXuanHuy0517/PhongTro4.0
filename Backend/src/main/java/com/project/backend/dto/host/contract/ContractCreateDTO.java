package com.project.backend.dto.host.contract;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến contract create để trao đổi giữa các tầng.
 */
@Data
public class ContractCreateDTO {
    private Long tenantId;
    private Long roomId;
    private Long depositId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal actualRentPrice;
    private BigDecimal elecPriceOverride;
    private BigDecimal waterPriceOverride;
    private String penaltyTerms;
}
