package com.project.backend.mapper.host;

import com.project.backend.entity.RoomAsset;
import com.project.backend.dto.host.equipment.RoomAssetResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Vai trò: Mapper của module host-service.
 * Chức năng: Chuyển đổi dữ liệu cho nghiệp vụ room asset giữa entity và DTO.
 */
@Component
public class RoomAssetMapper {

        /**
     * Chức năng: Chuyển đổi dto.
     */
public RoomAssetResponseDTO toDTO(RoomAsset roomAsset) {
        RoomAssetResponseDTO dto = new RoomAssetResponseDTO();
        dto.setEquipmentId(roomAsset.getEquipment().getEquipmentId());
        dto.setEquipmentName(roomAsset.getEquipment().getName());
        dto.setSerialNumber(roomAsset.getEquipment().getSerialNumber());
        dto.setStatus(roomAsset.getEquipment().getStatus());
        dto.setAssignedDate(roomAsset.getAssignedDate());
        dto.setNote(roomAsset.getNote());
        return dto;
    }
}
