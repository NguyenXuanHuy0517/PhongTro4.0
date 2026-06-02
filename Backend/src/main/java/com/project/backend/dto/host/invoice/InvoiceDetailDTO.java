package com.project.backend.dto.host.invoice;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến invoice detail để trao đổi giữa các tầng.
 */
@Data
public class InvoiceDetailDTO {
    private Long invoiceId;
    private String invoiceCode;
    private String tenantName;
    private String roomCode;
    private int billingMonth;
    private int billingYear;
    private LocalDate dueDate;
    private BigDecimal rentAmount;
    private int elecOld;
    private int elecNew;
    private BigDecimal elecPrice;
    private BigDecimal elecAmount;
    private int waterOld;
    private int waterNew;
    private BigDecimal waterPrice;
    private BigDecimal waterAmount;
    private BigDecimal serviceAmount;
    private List<String> serviceNames;
    private BigDecimal totalAmount;
    private String status;
    private String paymentProofUrl;
    private LocalDateTime paymentSubmittedAt;
    private String paymentNote;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}
