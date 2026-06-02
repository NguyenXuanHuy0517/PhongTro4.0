package com.project.backend.dto.host.contract;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ContractInviteResponseDTO {
    private String inviteCode;
    private LocalDateTime expiresAt;
    private String roomCode;
    private String areaName;
    private String areaAddress;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal actualRentPrice;
    private BigDecimal elecPriceOverride;
    private BigDecimal waterPriceOverride;
    private String penaltyTerms;
}
