package com.project.backend.service.host;

import com.project.backend.entity.Equipment;
import com.project.backend.entity.Room;
import com.project.backend.entity.RoomAsset;
import com.project.backend.entity.RoomAssetId;
import com.project.backend.repository.EquipmentRepository;
import com.project.backend.repository.RoomAssetRepository;
import com.project.backend.repository.RoomRepository;
import com.project.backend.dto.host.equipment.RoomAssetCreateDTO;
import com.project.backend.dto.host.equipment.RoomAssetResponseDTO;
import com.project.backend.dto.host.equipment.EquipmentDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.mapper.host.RoomAssetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Vai trò: Service xử lý nghiệp vụ của module host-service.
 * Chức năng: Chứa logic xử lý liên quan đến equipment.
 */
@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final RoomRepository roomRepository;
    private final RoomAssetRepository roomAssetRepository;
    private final RoomAssetMapper roomAssetMapper;

        /**
     * Chức năng: Lấy dữ liệu all equipments.
     */
public List<EquipmentDTO> getAllEquipments() {
        return equipmentRepository.findAll().stream()
                .map(e -> {
                    EquipmentDTO dto = new EquipmentDTO();
                    dto.setEquipmentId(e.getEquipmentId());
                    dto.setName(e.getName());
                    dto.setSerialNumber(e.getSerialNumber());
                    dto.setStatus(e.getStatus());
                    dto.setNote(e.getNote());
                    return dto;
                })
                .toList();
    }

        /**
     * Chức năng: Lấy dữ liệu assets by room.
     */
public List<RoomAssetResponseDTO> getAssetsByRoom(Long roomId) {
        return roomAssetRepository.findByRoom_RoomId(roomId).stream()
                .map(roomAssetMapper::toDTO)
                .toList();
    }

        /**
     * Chức năng: Thêm asset to room.
     */
public RoomAssetResponseDTO addAssetToRoom(RoomAssetCreateDTO request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng: " + request.getRoomId()));

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thiết bị: " + request.getEquipmentId()));

        RoomAssetId id = new RoomAssetId();
        id.setRoomId(room.getRoomId());
        id.setEquipmentId(equipment.getEquipmentId());

        RoomAsset roomAsset = new RoomAsset();
        roomAsset.setId(id);
        roomAsset.setRoom(room);
        roomAsset.setEquipment(equipment);
        roomAsset.setAssignedDate(request.getAssignedDate());
        roomAsset.setNote(request.getNote());

        roomAssetRepository.save(roomAsset);
        return roomAssetMapper.toDTO(roomAsset);
    }

        /**
     * Chức năng: Loại bỏ asset from room.
     */
public void removeAssetFromRoom(Long roomId, Long equipmentId) {
        roomAssetRepository.deleteByRoom_RoomIdAndEquipment_EquipmentId(roomId, equipmentId);
    }
}
