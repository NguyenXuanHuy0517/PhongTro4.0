package com.project.backend.mapper.host;

import com.project.backend.entity.Room;
import com.project.backend.dto.host.room.RoomResponseDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Vai trò: Mapper của module host-service.
 * Chức năng: Chuyển đổi dữ liệu cho nghiệp vụ room giữa entity và DTO.
 */
@Component
public class RoomMapper {

        /**
     * Chức năng: Chuyển đổi dto.
     */
public RoomResponseDTO toDTO(Room room) {
        RoomResponseDTO dto = new RoomResponseDTO();
        dto.setRoomId(room.getRoomId());
        dto.setRoomCode(room.getRoomCode());
        dto.setFloor(room.getFloor());
        dto.setBasePrice(room.getBasePrice());
        dto.setElecPrice(room.getElecPrice());
        dto.setWaterPrice(room.getWaterPrice());
        dto.setAreaSize(room.getAreaSize());
        dto.setStatus(room.getStatus());
        dto.setAmenities(room.getAmenities());
        dto.setImages(room.getImages());
        
        
        List<String> imagesList = parseJsonImages(room.getImages());
        dto.setImagesList(imagesList);
        
        dto.setDescription(room.getDescription());
        dto.setAreaId(room.getArea().getAreaId());
        dto.setAreaName(room.getArea().getAreaName());
        
        
        if (room.getArea().getHost() != null) {
            dto.setHostName(room.getArea().getHost().getFullName());
            dto.setHostAvatar(room.getArea().getHost().getAvatarUrl());
        }
        
        return dto;
    }

        /**
     * Chức năng: Phân tích json images.
     */
private List<String> parseJsonImages(String imagesJson) {
        List<String> result = new ArrayList<>();
        
        if (imagesJson == null || imagesJson.trim().isEmpty() || imagesJson.equals("[]")) {
            return result;
        }
        
        try {
            
            
            String cleaned = imagesJson.trim();
            if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
                cleaned = cleaned.substring(1, cleaned.length() - 1);
            }
            
            if (!cleaned.isEmpty()) {
                String[] urls = cleaned.split(",");
                for (String url : urls) {
                    String trimmed = url.trim();
                    
                    if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
                        trimmed = trimmed.substring(1, trimmed.length() - 1);
                    }
                    if (!trimmed.isEmpty()) {
                        result.add(trimmed);
                    }
                }
            }
        } catch (Exception e) {
            
        }
        
        return result;
    }
}
