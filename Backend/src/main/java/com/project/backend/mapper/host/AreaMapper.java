package com.project.backend.mapper.host;

import com.project.backend.entity.MotelArea;
import com.project.backend.dto.host.area.AreaResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Vai trò: Mapper của module host-service.
 * Chức năng: Chuyển đổi dữ liệu cho nghiệp vụ area giữa entity và DTO.
 */
@Component
public class AreaMapper {

        /**
     * Chức năng: Chuyển đổi dto.
     */
public AreaResponseDTO toDTO(MotelArea area, int totalRooms, int availableRooms, int rentedRooms, int maintenanceRooms) {
        AreaResponseDTO dto = new AreaResponseDTO();
        dto.setAreaId(area.getAreaId());
        dto.setAreaName(area.getAreaName());
        dto.setAddress(area.getAddress());
        dto.setWard(area.getWard());
        dto.setDistrict(area.getDistrict());
        dto.setCity(area.getCity());
        dto.setLatitude(area.getLatitude() != null ? area.getLatitude().doubleValue() : null);
        dto.setLongitude(area.getLongitude() != null ? area.getLongitude().doubleValue() : null);
        dto.setDescription(area.getDescription());
        dto.setTotalRooms(totalRooms);
        dto.setAvailableRooms(availableRooms);
        dto.setRentedRooms(rentedRooms);
        dto.setMaintenanceRooms(maintenanceRooms);
        return dto;
    }
}
