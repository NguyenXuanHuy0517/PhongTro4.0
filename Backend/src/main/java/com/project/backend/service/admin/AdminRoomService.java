package com.project.backend.service.admin;

import com.project.backend.dto.admin.room.AdminRoomAuditDTO;
import com.project.backend.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRoomService {

    private final RoomRepository roomRepository;

    public List<AdminRoomAuditDTO> getAllRooms() {
        return roomRepository.findAdminRoomAuditRows().stream()
                .map(row -> mapToAuditDTO(row, null))
                .toList();
    }

    public List<AdminRoomAuditDTO> getRoomsMissingInvoices() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate firstOfMonth = LocalDate.of(currentMonth.getYear(), currentMonth.getMonthValue(), 1);
        long daysWithoutInvoice = java.time.temporal.ChronoUnit.DAYS.between(firstOfMonth, LocalDate.now());

        return roomRepository.findAdminRoomsMissingInvoiceRows(
                        currentMonth.getMonthValue(),
                        currentMonth.getYear()
                ).stream()
                .map(row -> mapToAuditDTO(row, daysWithoutInvoice))
                .toList();
    }

    private AdminRoomAuditDTO mapToAuditDTO(Object[] row, Long daysWithoutInvoice) {
        AdminRoomAuditDTO dto = new AdminRoomAuditDTO();
        dto.setRoomId(asLong(row[0]));
        dto.setRoomCode((String) row[1]);
        dto.setAreaName((String) row[2]);
        dto.setHostName((String) row[3]);
        dto.setStatus((String) row[4]);
        dto.setBasePrice(asBigDecimal(row[5]));
        dto.setCurrentTenantName((String) row[6]);
        dto.setDaysWithoutInvoice(daysWithoutInvoice);
        return dto;
    }

    private Long asLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    private BigDecimal asBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        return BigDecimal.valueOf(((Number) value).doubleValue());
    }
}
