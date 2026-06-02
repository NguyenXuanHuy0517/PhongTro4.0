package com.project.backend.service.host;

import com.project.backend.dto.common.PagedResponse;
import com.project.backend.entity.Contract;
import com.project.backend.entity.MotelArea;
import com.project.backend.entity.Room;
import com.project.backend.entity.RoomStatusHistory;
import com.project.backend.entity.User;
import com.project.backend.repository.ContractRepository;
import com.project.backend.repository.MotelAreaRepository;
import com.project.backend.repository.RoomRepository;
import com.project.backend.repository.RoomStatusHistoryRepository;
import com.project.backend.dto.host.room.RoomCreateDTO;
import com.project.backend.dto.host.room.RoomResponseDTO;
import com.project.backend.dto.host.room.RoomStatusUpdateDTO;
import com.project.backend.dto.host.room.RoomUpdateDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.mapper.host.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private static final Set<String> ROOM_SORT_FIELDS = Set.of(
            "roomCode", "basePrice", "status", "createdAt", "updatedAt"
    );

    private final RoomRepository roomRepository;
    private final MotelAreaRepository areaRepository;
    private final RoomStatusHistoryRepository statusHistoryRepository;
    private final ContractRepository contractRepository;
    private final RoomMapper roomMapper;

    public List<RoomResponseDTO> getRoomsByHost(Long hostId) {
        List<Room> rooms = roomRepository.findWithAreaAndHostByHostId(hostId);
        Map<Long, Contract> activeContractsByRoomId = contractRepository
                .findWithRelationsByHostIdAndStatus(hostId, "ACTIVE").stream()
                .collect(Collectors.toMap(
                        contract -> contract.getRoom().getRoomId(),
                        Function.identity(),
                        (first, second) -> first
                ));

        return rooms.stream()
                .map(room -> enrichRoom(room, activeContractsByRoomId.get(room.getRoomId())))
                .toList();
    }

    public PagedResponse<RoomResponseDTO> getRoomsPage(
            Long hostId,
            Long areaId,
            String status,
            String search,
            int page,
            int size,
            String sort
    ) {
        Page<Room> roomPage = roomRepository.findPageWithAreaAndHostByHostId(
                hostId,
                areaId,
                normalize(status),
                normalize(search),
                buildPageable(page, size, sort)
        );

        List<Long> roomIds = roomPage.getContent().stream()
                .map(Room::getRoomId)
                .toList();

        Map<Long, Contract> activeContractsByRoomId = roomIds.isEmpty()
                ? Map.of()
                : contractRepository.findWithRelationsByRoomIdsAndStatus(roomIds, "ACTIVE").stream()
                .collect(Collectors.toMap(
                        contract -> contract.getRoom().getRoomId(),
                        Function.identity(),
                        (first, second) -> first
                ));

        List<RoomResponseDTO> items = roomPage.getContent().stream()
                .map(room -> enrichRoom(room, activeContractsByRoomId.get(room.getRoomId())))
                .toList();

        return PagedResponse.from(roomPage, items);
    }

    public List<RoomResponseDTO> getRoomsByArea(Long areaId) {
        List<Room> rooms = roomRepository.findWithAreaAndHostByAreaId(areaId);
        Map<Long, Contract> activeContractsByRoomId = contractRepository
                .findWithRelationsByAreaIdAndStatus(areaId, "ACTIVE").stream()
                .collect(Collectors.toMap(
                        contract -> contract.getRoom().getRoomId(),
                        Function.identity(),
                        (first, second) -> first
                ));

        return rooms.stream()
                .map(room -> enrichRoom(room, activeContractsByRoomId.get(room.getRoomId())))
                .toList();
    }

    public RoomResponseDTO getRoomDetail(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay phong: " + roomId));
        RoomResponseDTO dto = roomMapper.toDTO(room);
        contractRepository.findByRoom_RoomIdAndStatus(roomId, "ACTIVE")
                .ifPresent(contract -> {
                    dto.setCurrentTenantName(contract.getTenant().getFullName());
                    dto.setCurrentContractId(contract.getContractId());
                });
        return dto;
    }

    public RoomResponseDTO createRoom(RoomCreateDTO request) {
        MotelArea area = areaRepository.findById(request.getAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay khu tro: " + request.getAreaId()));

        Room room = new Room();
        room.setArea(area);
        room.setRoomCode(request.getRoomCode());
        room.setFloor(request.getFloor());
        room.setBasePrice(request.getBasePrice());
        room.setElecPrice(request.getElecPrice());
        room.setWaterPrice(request.getWaterPrice());
        room.setAreaSize(request.getAreaSize());
        room.setAmenities(request.getAmenities());
        room.setImages(request.getImages());
        room.setDescription(request.getDescription());
        room.setStatus("AVAILABLE");

        roomRepository.save(room);
        return roomMapper.toDTO(room);
    }

    public RoomResponseDTO updateRoom(Long roomId, RoomUpdateDTO request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay phong: " + roomId));

        room.setBasePrice(request.getBasePrice());
        room.setElecPrice(request.getElecPrice());
        room.setWaterPrice(request.getWaterPrice());
        room.setAreaSize(request.getAreaSize());
        room.setAmenities(request.getAmenities());
        room.setImages(request.getImages());
        room.setDescription(request.getDescription());

        roomRepository.save(room);
        return roomMapper.toDTO(room);
    }

    public void updateStatus(Long roomId, RoomStatusUpdateDTO request, User changedBy) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay phong: " + roomId));

        String oldStatus = room.getStatus();
        room.setStatus(request.getStatus());
        roomRepository.save(room);

        RoomStatusHistory history = new RoomStatusHistory();
        history.setRoom(room);
        history.setOldStatus(oldStatus);
        history.setNewStatus(request.getStatus());
        history.setChangedBy(changedBy);
        history.setNote(request.getNote());
        statusHistoryRepository.save(history);
    }

    public List<RoomStatusHistory> getStatusHistory(Long roomId) {
        return statusHistoryRepository.findByRoom_RoomIdOrderByChangedAtDesc(roomId);
    }

    private RoomResponseDTO enrichRoom(Room room, Contract activeContract) {
        RoomResponseDTO dto = roomMapper.toDTO(room);
        if (activeContract != null) {
            dto.setCurrentTenantName(activeContract.getTenant().getFullName());
            dto.setCurrentContractId(activeContract.getContractId());
        }
        return dto;
    }

    private Pageable buildPageable(int page, int size, String sort) {
        String[] sortParts = (sort == null ? "" : sort).split(",", 2);
        String requestedField = sortParts.length > 0 ? sortParts[0].trim() : "";
        String field = ROOM_SORT_FIELDS.contains(requestedField) ? requestedField : "roomCode";
        Sort.Direction direction = sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1].trim())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(direction, field));
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
