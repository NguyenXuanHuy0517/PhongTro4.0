package com.project.backend.dto.host.contract;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến contract để trao đổi giữa các tầng.
 */
@Data
public class ContractResponseDTO {
    private Long contractId;
    private String contractCode;
    private String tenantName;
    private String roomCode;
    private String areaName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal actualRentPrice;
    private BigDecimal elecPriceOverride;
    private BigDecimal waterPriceOverride;
    private String status;
    private BigDecimal depositAmount;
    private String depositStatus;
    private LocalDateTime depositDate;

    
    private List<ContractServiceDTO> contractServices;

    
    private List<String> serviceNames;
}
