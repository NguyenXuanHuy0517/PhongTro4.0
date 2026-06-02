package com.project.backend.controller.admin;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.dto.admin.room.AdminRoomAuditDTO;
import com.project.backend.service.admin.AdminRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vai trò: REST controller của module admin-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ admin room và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/rooms")
@RequiredArgsConstructor
public class AdminRoomController {

    private final AdminRoomService roomService;

        /**
     * Chức năng: Lấy dữ liệu all rooms.
     * URL: GET /api/admin/rooms
     */
@GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AdminRoomAuditDTO>>> getAllRooms() {
        log.info("GET /api/admin/rooms");
        List<AdminRoomAuditDTO> rooms = roomService.getAllRooms();
        log.info("GET /api/admin/rooms - trả về {} phòng", rooms.size());
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

        /**
     * Chức năng: Lấy dữ liệu rooms without invoice.
     * URL: GET /api/admin/rooms/without-invoice
     */
@GetMapping("/without-invoice")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AdminRoomAuditDTO>>> getRoomsWithoutInvoice() {
        log.info("GET /api/admin/rooms/without-invoice");
        List<AdminRoomAuditDTO> rooms = roomService.getRoomsMissingInvoices();
        log.info("GET /api/admin/rooms/without-invoice - trả về {} phòng", rooms.size());
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }
}
