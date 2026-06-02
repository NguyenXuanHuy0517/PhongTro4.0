package com.project.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "invoices",
        indexes = {
                @Index(name = "idx_invoices_contract_period_status", columnList = "contract_id,billing_month,billing_year,status")
        }
)
@Data
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Column(name = "invoice_code", nullable = false, unique = true, length = 60)
    private String invoiceCode;

    @Column(name = "billing_month", nullable = false)
    private Integer billingMonth;

    @Column(name = "billing_year", nullable = false)
    private Integer billingYear;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "elec_old", nullable = false)
    private Integer elecOld = 0;

    @Column(name = "elec_new", nullable = false)
    private Integer elecNew = 0;

    @Column(name = "elec_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal elecPrice = BigDecimal.ZERO;

    @Column(name = "water_old", nullable = false)
    private Integer waterOld = 0;

    @Column(name = "water_new", nullable = false)
    private Integer waterNew = 0;

    @Column(name = "water_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal waterPrice = BigDecimal.ZERO;

    @Column(name = "rent_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal rentAmount = BigDecimal.ZERO;

    @Column(name = "elec_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal elecAmount = BigDecimal.ZERO;

    @Column(name = "water_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal waterAmount = BigDecimal.ZERO;

    @Column(name = "service_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal serviceAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "DRAFT";    // DRAFT, UNPAID, PAID, OVERDUE

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by")
    private User paidBy;

    @Column(name = "payment_proof_url", length = 500)
    private String paymentProofUrl;

    @Column(name = "payment_submitted_at")
    private LocalDateTime paymentSubmittedAt;

    @Column(name = "payment_note", columnDefinition = "TEXT")
    private String paymentNote;

    @Column(name = "payment_status", length = 30)
    private String paymentStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
