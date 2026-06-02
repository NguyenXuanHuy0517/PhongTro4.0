package com.project.backend.controller.host;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.dto.host.equipment.EquipmentDTO;
import com.project.backend.dto.host.equipment.RoomAssetCreateDTO;
import com.project.backend.dto.host.equipment.RoomAssetResponseDTO;
import com.project.backend.service.host.EquipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vai trò: REST controller của module host-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ equipment và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/host")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

        /**
     * Chức năng: Lấy dữ liệu all equipments.
     * URL: GET /api/host/equipments
     */
@GetMapping("/equipments")
    public ResponseEntity<ApiResponse<List<EquipmentDTO>>> getAllEquipments() {
        log.info("GET /api/host/equipments");
        List<EquipmentDTO> result = equipmentService.getAllEquipments();
        log.info("GET /api/host/equipments - trả về {} thiết bị", result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Lấy dữ liệu assets by room.
     * URL: GET /api/host/rooms/{roomId}/assets
     */
@GetMapping("/rooms/{roomId}/assets")
    public ResponseEntity<ApiResponse<List<RoomAssetResponseDTO>>> getAssetsByRoom(@PathVariable Long roomId) {
        log.info("GET /api/host/rooms/{}/assets", roomId);
        List<RoomAssetResponseDTO> result = equipmentService.getAssetsByRoom(roomId);
        log.info("GET /api/host/rooms/{}/assets - trả về {} thiết bị", roomId, result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Thêm asset.
     * URL: POST /api/host/rooms/{roomId}/assets
     */
@PostMapping("/rooms/{roomId}/assets")
    public ResponseEntity<ApiResponse<RoomAssetResponseDTO>> addAsset(@PathVariable Long roomId,
                                                                      @RequestBody RoomAssetCreateDTO request) {
        log.info("POST /api/host/rooms/{}/assets - equipmentId: {}", roomId, request.getEquipmentId());
        request.setRoomId(roomId);
        RoomAssetResponseDTO result = equipmentService.addAssetToRoom(request);
        log.info("POST /api/host/rooms/{}/assets - gán thành công", roomId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

        /**
     * Chức năng: Loại bỏ asset.
     * URL: DELETE /api/host/rooms/{roomId}/assets/{equipmentId}
     */
@DeleteMapping("/rooms/{roomId}/assets/{equipmentId}")
    public ResponseEntity<ApiResponse<Void>> removeAsset(@PathVariable Long roomId,
                                                         @PathVariable Long equipmentId) {
        log.info("DELETE /api/host/rooms/{}/assets/{}", roomId, equipmentId);
        equipmentService.removeAssetFromRoom(roomId, equipmentId);
        log.info("DELETE /api/host/rooms/{}/assets/{} - gỡ thành công", roomId, equipmentId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
