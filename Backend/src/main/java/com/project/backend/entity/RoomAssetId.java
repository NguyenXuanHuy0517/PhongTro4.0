package com.project.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

// Composite key
@Embeddable
@Data
public class RoomAssetId implements Serializable {
    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "equipment_id")
    private Long equipmentId;
}
