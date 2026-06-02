package com.project.backend.mapper.host;

import com.project.backend.entity.Deposit;
import com.project.backend.dto.host.deposit.DepositResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Vai trò: Mapper của module host-service.
 * Chức năng: Chuyển đổi dữ liệu cho nghiệp vụ deposit giữa entity và DTO.
 */
@Component
public class DepositMapper {

        /**
     * Chức năng: Chuyển đổi dto.
     */
public DepositResponseDTO toDTO(Deposit deposit) {
        DepositResponseDTO dto = new DepositResponseDTO();
        dto.setDepositId(deposit.getDepositId());
        dto.setTenantId(deposit.getTenant().getUserId());
        dto.setRoomId(deposit.getRoom().getRoomId());
        dto.setTenantName(deposit.getTenant().getFullName());
        dto.setRoomCode(deposit.getRoom().getRoomCode());
        dto.setAmount(deposit.getAmount());
        dto.setExpectedCheckIn(deposit.getExpectedCheckIn());
        dto.setStatus(deposit.getStatus());
        dto.setNote(deposit.getNote());
        dto.setDepositDate(deposit.getDepositDate());
        return dto;
    }
}
