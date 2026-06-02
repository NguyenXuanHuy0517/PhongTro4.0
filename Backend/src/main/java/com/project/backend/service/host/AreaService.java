package com.project.backend.service.host;

import com.project.backend.entity.MotelArea;
import com.project.backend.entity.User;
import com.project.backend.repository.MotelAreaRepository;
import com.project.backend.repository.RoomRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.dto.host.area.AreaCreateDTO;
import com.project.backend.dto.host.area.AreaResponseDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.mapper.host.AreaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Vai trò: Service xử lý nghiệp vụ của module host-service.
 * Chức năng: Chứa logic xử lý liên quan đến area.
 */
@Service
@RequiredArgsConstructor
public class AreaService {

    private final MotelAreaRepository areaRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final AreaMapper areaMapper;

        /**
     * Chức năng: Lấy dữ liệu areas by host.
     */
public List<AreaResponseDTO> getAreasByHost(Long hostId) {
        return areaRepository.findByHost_UserId(hostId).stream()
                .map(area -> areaMapper.toDTO(
                        area,
                        roomRepository.countByArea_AreaIdAndStatus(area.getAreaId(), "RENTED") +
                                roomRepository.countByArea_AreaIdAndStatus(area.getAreaId(), "AVAILABLE") +
                                roomRepository.countByArea_AreaIdAndStatus(area.getAreaId(), "MAINTENANCE") +
                                roomRepository.countByArea_AreaIdAndStatus(area.getAreaId(), "DEPOSITED"),
                        roomRepository.countByArea_AreaIdAndStatus(area.getAreaId(), "AVAILABLE"),
                        roomRepository.countByArea_AreaIdAndStatus(area.getAreaId(), "RENTED"),
                        roomRepository.countByArea_AreaIdAndStatus(area.getAreaId(), "MAINTENANCE")
                ))
                .toList();
    }

        /**
     * Chức năng: Tạo area.
     */
public AreaResponseDTO createArea(Long hostId, AreaCreateDTO request) {
        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy host: " + hostId));

        MotelArea area = new MotelArea();
        area.setHost(host);
        area.setAreaName(request.getAreaName());
        area.setAddress(request.getAddress());
        area.setWard(request.getWard());
        area.setDistrict(request.getDistrict());
        area.setCity(request.getCity());
        area.setLatitude(request.getLatitude() != null ? BigDecimal.valueOf(request.getLatitude()) : null);
        area.setLongitude(request.getLongitude() != null ? BigDecimal.valueOf(request.getLongitude()) : null);
        area.setDescription(request.getDescription());

        areaRepository.save(area);
        return areaMapper.toDTO(area, 0, 0, 0, 0);
    }

        /**
     * Chức năng: Cập nhật area.
     */
public AreaResponseDTO updateArea(Long areaId, AreaCreateDTO request) {
        MotelArea area = areaRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu trọ: " + areaId));

        area.setAreaName(request.getAreaName());
        area.setAddress(request.getAddress());
        area.setWard(request.getWard());
        area.setDistrict(request.getDistrict());
        area.setCity(request.getCity());
        area.setLatitude(request.getLatitude() != null ? BigDecimal.valueOf(request.getLatitude()) : null);
        area.setLongitude(request.getLongitude() != null ? BigDecimal.valueOf(request.getLongitude()) : null);
        area.setDescription(request.getDescription());

        areaRepository.save(area);
        return areaMapper.toDTO(
                area,
                roomRepository.countByArea_AreaIdAndStatus(areaId, "RENTED") +
                        roomRepository.countByArea_AreaIdAndStatus(areaId, "AVAILABLE") +
                        roomRepository.countByArea_AreaIdAndStatus(areaId, "MAINTENANCE") +
                        roomRepository.countByArea_AreaIdAndStatus(areaId, "DEPOSITED"),
                roomRepository.countByArea_AreaIdAndStatus(areaId, "AVAILABLE"),
                roomRepository.countByArea_AreaIdAndStatus(areaId, "RENTED"),
                roomRepository.countByArea_AreaIdAndStatus(areaId, "MAINTENANCE")
        );
    }
}
