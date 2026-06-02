package com.project.backend.dto.tenant.invoice;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Vai trò: DTO của module tenant-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến my invoice để trao đổi giữa các tầng.
 */
@Data
public class MyInvoiceDTO {
    private Long invoiceId;
    private String invoiceCode;
    private String roomCode;
    private int billingMonth;
    private int billingYear;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime paymentSubmittedAt;
    private String paymentProofUrl;
    private String paymentStatus;
    private LocalDateTime createdAt;
}
