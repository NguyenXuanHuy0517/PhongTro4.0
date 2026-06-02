package com.project.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "rooms",
        indexes = {
                @Index(name = "idx_rooms_area_status", columnList = "area_id,status")
        }
)
@Data
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private MotelArea area;

    @Column(name = "room_code", nullable = false, length = 50)
    private String roomCode;

    @Column(name = "floor", nullable = false)
    private Integer floor = 1;

    @Column(name = "area_size", precision = 6, scale = 2)
    private BigDecimal areaSize;

    @Column(name = "base_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "elec_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal elecPrice = new BigDecimal("3500.00");

    @Column(name = "water_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal waterPrice = new BigDecimal("15000.00");

    @Column(name = "status", nullable = false, length = 20)
    private String status = "AVAILABLE";    // AVAILABLE, DEPOSITED, RENTED, MAINTENANCE

    @Column(name = "amenities", columnDefinition = "JSON")
    private String amenities;

    @Column(name = "images", columnDefinition = "JSON")
    private String images;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

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
