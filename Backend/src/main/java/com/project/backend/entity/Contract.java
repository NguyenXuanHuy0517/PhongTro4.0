package com.project.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "contracts",
        indexes = {
                @Index(name = "idx_contracts_room_status_tenant", columnList = "room_id,status,tenant_id")
        }
)
@Data
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long contractId;

    @Column(name = "contract_code", nullable = false, unique = true, length = 50)
    private String contractCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposit_id")
    private Deposit deposit;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "actual_rent_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal actualRentPrice;

    @Column(name = "elec_price_override", precision = 10, scale = 2)
    private BigDecimal elecPriceOverride;

    @Column(name = "water_price_override", precision = 10, scale = 2)
    private BigDecimal waterPriceOverride;

    @Column(name = "penalty_terms", columnDefinition = "TEXT")
    private String penaltyTerms;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";   // ACTIVE, EXPIRED, TERMINATED_EARLY

    @Column(name = "digital_signature_url", length = 500)
    private String digitalSignatureUrl;

    @Column(name = "terminated_at")
    private LocalDateTime terminatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminated_by")
    private User terminatedBy;

    @Column(name = "termination_reason", columnDefinition = "TEXT")
    private String terminationReason;

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
