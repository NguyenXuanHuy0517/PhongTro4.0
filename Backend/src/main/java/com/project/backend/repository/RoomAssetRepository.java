package com.project.backend.repository;

import com.project.backend.entity.RoomAsset;
import com.project.backend.entity.RoomAssetId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomAssetRepository extends JpaRepository<RoomAsset, RoomAssetId> {
    List<RoomAsset> findByRoom_RoomId(Long roomId);
    void deleteByRoom_RoomIdAndEquipment_EquipmentId(Long roomId, Long equipmentId);
}