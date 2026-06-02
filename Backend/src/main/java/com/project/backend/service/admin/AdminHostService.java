package com.project.backend.service.admin;

import com.project.backend.dto.admin.host.AdminHostDetailDTO;
import com.project.backend.dto.admin.host.AdminHostResponseDTO;
import com.project.backend.dto.admin.host.AdminHostStatusUpdateRequest;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.entity.User;
import com.project.backend.repository.ContractRepository;
import com.project.backend.repository.InvoiceRepository;
import com.project.backend.repository.MotelAreaRepository;
import com.project.backend.repository.RoomRepository;
import com.project.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminHostService {

    private final UserRepository userRepository;
    private final MotelAreaRepository areaRepository;
    private final RoomRepository roomRepository;
    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;

    public List<AdminHostResponseDTO> getAllHosts() {
        List<User> hosts = userRepository.findByRole_RoleName("HOST");
        List<Long> hostIds = hosts.stream()
                .map(User::getUserId)
                .toList();

        Map<Long, Long> areaCountsByHostId = hostIds.isEmpty()
                ? Map.of()
                : toCountMap(areaRepository.countAreasByHostIds(hostIds));
        Map<Long, Long> roomCountsByHostId = hostIds.isEmpty()
                ? Map.of()
                : toCountMap(roomRepository.countRoomsByHostIds(hostIds));

        return hosts.stream()
                .map(host -> mapToListDTO(
                        host,
                        areaCountsByHostId.getOrDefault(host.getUserId(), 0L),
                        roomCountsByHostId.getOrDefault(host.getUserId(), 0L)
                ))
                .toList();
    }

    public AdminHostDetailDTO getHostDetail(Long hostId) {
        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay host: " + hostId));

        if (!"HOST".equals(host.getRole().getRoleName())) {
            throw new IllegalArgumentException("User nay khong phai host");
        }

        return mapToDetailDTO(host);
    }

    @Transactional
    public void updateHostStatus(Long hostId, AdminHostStatusUpdateRequest request) {
        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay host: " + hostId));

        if (!"HOST".equals(host.getRole().getRoleName())) {
            throw new IllegalArgumentException("User nay khong phai host");
        }

        if ("ADMIN".equals(host.getRole().getRoleName())) {
            throw new IllegalArgumentException("Khong the khoa tai khoan ADMIN");
        }

        if (host.isActive() == request.isActive()) {
            log.warn("Host {} da o trang thai target {}", hostId, request.isActive());
            return;
        }

        host.setActive(request.isActive());
        userRepository.save(host);

        log.info("Host {} status updated to {} by admin. Reason: {}. Note: {}",
                hostId, request.isActive(), request.getReason(), request.getNote());
    }

    private AdminHostResponseDTO mapToListDTO(User host, Long totalAreas, Long totalRooms) {
        AdminHostResponseDTO dto = new AdminHostResponseDTO();
        dto.setUserId(host.getUserId());
        dto.setFullName(host.getFullName());
        dto.setEmail(host.getEmail());
        dto.setPhoneNumber(host.getPhoneNumber());
        dto.setAvatarUrl(host.getAvatarUrl());
        dto.setActive(host.isActive());
        dto.setTotalAreas(totalAreas);
        dto.setTotalRooms(totalRooms);
        return dto;
    }

    private AdminHostDetailDTO mapToDetailDTO(User host) {
        AdminHostDetailDTO dto = new AdminHostDetailDTO();
        dto.setUserId(host.getUserId());
        dto.setFullName(host.getFullName());
        dto.setEmail(host.getEmail());
        dto.setPhoneNumber(host.getPhoneNumber());
        dto.setAvatarUrl(host.getAvatarUrl());
        dto.setActive(host.isActive());
        dto.setCreatedAt(host.getCreatedAt());
        dto.setTotalAreas(areaRepository.countByHost_UserId(host.getUserId()));
        dto.setTotalRooms(roomRepository.countByArea_Host_UserId(host.getUserId()));
        dto.setActiveContracts(contractRepository.countByRoom_Area_Host_UserIdAndStatus(host.getUserId(), "ACTIVE"));
        dto.setOverdueInvoices(invoiceRepository.countOverdueByHostId(host.getUserId()));

        YearMonth currentMonth = YearMonth.now();
        dto.setRoomsWithoutInvoice(roomRepository.countRoomsWithoutInvoiceByHostId(
                host.getUserId(),
                currentMonth.getMonthValue(),
                currentMonth.getYear()
        ));
        dto.setLatestStatusReason(null);
        return dto;
    }

    private Map<Long, Long> toCountMap(List<Object[]> rows) {
        return rows.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }
}
