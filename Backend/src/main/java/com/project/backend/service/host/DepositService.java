package com.project.backend.service.host;

import com.project.backend.entity.Deposit;
import com.project.backend.entity.Room;
import com.project.backend.entity.User;
import com.project.backend.repository.DepositRepository;
import com.project.backend.repository.RoomRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.dto.host.deposit.DepositCreateDTO;
import com.project.backend.dto.host.deposit.DepositResponseDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.mapper.host.DepositMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Vai trò: Service xử lý nghiệp vụ của module host-service.
 * Chức năng: Chứa logic xử lý liên quan đến deposit.
 */
@Service
@RequiredArgsConstructor
public class DepositService {

    private final DepositRepository depositRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final DepositMapper depositMapper;

        /**
     * Chức năng: Lấy dữ liệu deposits by host.
     */
public List<DepositResponseDTO> getDepositsByHost(Long hostId) {
        return depositRepository.findByRoom_Area_Host_UserId(hostId).stream()
                .map(depositMapper::toDTO)
                .toList();
    }

    

        /**
     * Chức năng: Tạo deposit.
     */
@Transactional
    public DepositResponseDTO createDeposit(DepositCreateDTO request) {
        User tenant = userRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người thuê: " + request.getTenantId()));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng: " + request.getRoomId()));

        if (!"AVAILABLE".equals(room.getStatus())) {
            throw new IllegalStateException(
                    "Phòng " + room.getRoomCode() + " hiện không ở trạng thái AVAILABLE.");
        }

        Deposit deposit = new Deposit();
        deposit.setTenant(tenant);
        deposit.setRoom(room);
        deposit.setAmount(request.getAmount());
        deposit.setExpectedCheckIn(request.getExpectedCheckIn());
        deposit.setNote(request.getNote());
        deposit.setStatus("PENDING");

        room.setStatus("DEPOSITED");
        roomRepository.save(room);
        depositRepository.save(deposit);

        return depositMapper.toDTO(deposit);
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ confirm deposit.
     */
public void confirmDeposit(Long depositId, User confirmedBy) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt cọc: " + depositId));
        deposit.setStatus("CONFIRMED");
        deposit.setConfirmedBy(confirmedBy);
        deposit.setConfirmedAt(LocalDateTime.now());
        depositRepository.save(deposit);
    }

    

        /**
     * Chức năng: Thực hiện nghiệp vụ refund deposit.
     */
@Transactional
    public void refundDeposit(Long depositId) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt cọc: " + depositId));
        deposit.setStatus("REFUNDED");

        Room room = deposit.getRoom();
        room.setStatus("AVAILABLE");
        roomRepository.save(room);
        depositRepository.save(deposit);
    }
}
