package com.project.backend.controller.host;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.dto.common.PagedResponse;
import com.project.backend.entity.RoomStatusHistory;
import com.project.backend.entity.User;
import com.project.backend.dto.host.room.RoomCreateDTO;
import com.project.backend.dto.host.room.RoomResponseDTO;
import com.project.backend.dto.host.room.RoomStatusUpdateDTO;
import com.project.backend.dto.host.room.RoomUpdateDTO;
import com.project.backend.service.host.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/host/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponseDTO>>> getRooms(@RequestParam Long hostId) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getRoomsByHost(hostId)));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<RoomResponseDTO>>> getRoomsPaged(
            @RequestParam Long hostId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "roomCode,asc") String sort) {
        return ResponseEntity.ok(ApiResponse.success(
                roomService.getRoomsPage(hostId, areaId, status, search, page, size, sort)
        ));
    }

    @GetMapping("/area/{areaId}")
    public ResponseEntity<ApiResponse<List<RoomResponseDTO>>> getRoomsByArea(@PathVariable Long areaId) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getRoomsByArea(areaId)));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponseDTO>> getRoomDetail(@PathVariable Long roomId) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getRoomDetail(roomId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponseDTO>> createRoom(@RequestBody RoomCreateDTO request) {
        return ResponseEntity.ok(ApiResponse.success(roomService.createRoom(request)));
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponseDTO>> updateRoom(
            @PathVariable Long roomId,
            @RequestBody RoomUpdateDTO request) {
        return ResponseEntity.ok(ApiResponse.success(roomService.updateRoom(roomId, request)));
    }

    @PatchMapping("/{roomId}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable Long roomId,
            @RequestBody RoomStatusUpdateDTO request,
            @RequestParam Long changedById) {
        User changedBy = new User();
        changedBy.setUserId(changedById);
        roomService.updateStatus(roomId, request, changedBy);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{roomId}/history")
    public ResponseEntity<ApiResponse<List<RoomStatusHistory>>> getStatusHistory(@PathVariable Long roomId) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getStatusHistory(roomId)));
    }
}
