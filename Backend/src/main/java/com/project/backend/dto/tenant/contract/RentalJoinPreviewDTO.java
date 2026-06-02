package com.project.backend.dto.tenant.contract;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RentalJoinPreviewDTO {
    private String roomCode;
    private String areaName;
    private String areaAddress;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal actualRentPrice;
    private BigDecimal elecPrice;
    private BigDecimal waterPrice;
    private String penaltyTerms;
    private LocalDateTime expiresAt;
}
