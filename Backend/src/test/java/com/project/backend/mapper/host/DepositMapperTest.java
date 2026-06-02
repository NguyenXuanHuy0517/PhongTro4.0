package com.project.backend.mapper.host;

import com.project.backend.dto.host.deposit.DepositResponseDTO;
import com.project.backend.entity.Deposit;
import com.project.backend.entity.Room;
import com.project.backend.entity.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DepositMapperTest {

    private final DepositMapper mapper = new DepositMapper();

    @Test
    void toDTOIncludesTenantAndRoomIdsForFrontendFollowUpActions() {
        User tenant = new User();
        tenant.setUserId(11L);
        tenant.setFullName("Nguyen Van A");

        Room room = new Room();
        room.setRoomId(22L);
        room.setRoomCode("P101");

        Deposit deposit = new Deposit();
        deposit.setDepositId(33L);
        deposit.setTenant(tenant);
        deposit.setRoom(room);
        deposit.setAmount(new BigDecimal("1500000"));
        deposit.setExpectedCheckIn(LocalDate.of(2026, 5, 20));
        deposit.setStatus("CONFIRMED");
        deposit.setNote("Da nhan chuyen khoan");
        deposit.setDepositDate(LocalDateTime.of(2026, 5, 11, 9, 30));

        DepositResponseDTO dto = mapper.toDTO(deposit);

        assertThat(dto.getDepositId()).isEqualTo(33L);
        assertThat(dto.getTenantId()).isEqualTo(11L);
        assertThat(dto.getRoomId()).isEqualTo(22L);
        assertThat(dto.getTenantName()).isEqualTo("Nguyen Van A");
        assertThat(dto.getRoomCode()).isEqualTo("P101");
        assertThat(dto.getAmount()).isEqualByComparingTo("1500000");
        assertThat(dto.getStatus()).isEqualTo("CONFIRMED");
    }
}
