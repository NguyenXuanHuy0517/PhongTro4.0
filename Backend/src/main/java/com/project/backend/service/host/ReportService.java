package com.project.backend.service.host;

import com.project.backend.repository.*;
import com.project.backend.dto.host.report.ReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Vai trò: Service xử lý nghiệp vụ của module host-service.
 * Chức năng: Chứa logic xử lý liên quan đến report.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final RoomRepository roomRepository;
    private final InvoiceRepository invoiceRepository;
    private final IssueRepository issueRepository;

        /**
     * Chức năng: Lấy dữ liệu dashboard.
     */
public ReportDTO getDashboard(Long hostId) {
        int totalRooms = Math.toIntExact(roomRepository.countByArea_Host_UserId(hostId));
        Map<String, Long> roomStatusCounts = roomRepository.countRoomStatusByHost(hostId).stream()
                .collect(Collectors.toMap(
                        row -> String.valueOf(row[0]),
                        row -> (Long) row[1]
                ));
        int rentedRooms = Math.toIntExact(roomStatusCounts.getOrDefault("RENTED", 0L));
        int availableRooms = Math.toIntExact(roomStatusCounts.getOrDefault("AVAILABLE", 0L));
        int maintenanceRooms = Math.toIntExact(roomStatusCounts.getOrDefault("MAINTENANCE", 0L));

        LocalDate now = LocalDate.now();
        BigDecimal totalRevenue = invoiceRepository.sumRevenueByHostAndPeriod(
                hostId,
                now.getMonthValue(),
                now.getYear()
        );

        LocalDate previousMonth = now.minusMonths(1);
        BigDecimal previousRevenue = invoiceRepository.sumRevenueByHostAndPeriod(
                hostId,
                previousMonth.getMonthValue(),
                previousMonth.getYear()
        );

        int overdueCount = Math.toIntExact(
                invoiceRepository.countByContract_Room_Area_Host_UserIdAndStatus(hostId, "OVERDUE")
        );

        int openIssueCount = Math.toIntExact(
                issueRepository.countByRoom_Area_Host_UserIdAndStatusIn(hostId, List.of("OPEN", "PROCESSING"))
        );

        ReportDTO dto = new ReportDTO();
        dto.setTotalRevenue(totalRevenue);
        dto.setPreviousRevenue(previousRevenue);
        dto.setTotalRooms(totalRooms);
        dto.setRentedRooms(rentedRooms);
        dto.setAvailableRooms(availableRooms);
        dto.setMaintenanceRooms(maintenanceRooms);
        dto.setOccupancyRate(totalRooms > 0 ? (double) rentedRooms / totalRooms * 100 : 0);
        dto.setOverdueCount(overdueCount);
        dto.setOpenIssueCount(openIssueCount);
        dto.setTopServices(List.of());
        return dto;
    }
}
