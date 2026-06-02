package com.project.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "deposits")
@Data
public class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deposit_id")
    private Long depositId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "expected_check_in")
    private LocalDate expectedCheckIn;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";  // PENDING, CONFIRMED, COMPLETED, EXPIRED, REFUNDED, FORFEITED

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by")
    private User confirmedBy;

    @Column(name = "deposit_date", nullable = false, updatable = false)
    private LocalDateTime depositDate;

    @PrePersist
    protected void onCreate() {
        depositDate = LocalDateTime.now();
    }
}