package com.project.backend.service.admin;

import com.project.backend.dto.admin.common.AdminAlertDTO;
import com.project.backend.dto.admin.dashboard.AdminDashboardDTO;
import com.project.backend.repository.ContractRepository;
import com.project.backend.repository.InvoiceRepository;
import com.project.backend.repository.RoomRepository;
import com.project.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;

    public AdminDashboardDTO getDashboard() {
        AdminDashboardDTO dashboard = new AdminDashboardDTO();
        dashboard.setTotalUsers(userRepository.count());
        dashboard.setTotalHosts(userRepository.countByRole_RoleName("HOST"));
        dashboard.setTotalTenants(userRepository.countByRole_RoleName("TENANT"));
        dashboard.setTotalRooms(roomRepository.countTotalRooms());
        dashboard.setTotalContracts(contractRepository.count());

        Long rentedRooms = roomRepository.countRentedRooms();
        Long totalRooms = dashboard.getTotalRooms();
        long occupancyRate = (totalRooms > 0) ? (rentedRooms * 100) / totalRooms : 0;
        dashboard.setOccupancyRate(occupancyRate);

        BigDecimal totalRevenue = invoiceRepository.sumRevenueAllTime();
        dashboard.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        YearMonth now = YearMonth.now();
        BigDecimal thisMonthRevenue = invoiceRepository.sumRevenueByPeriod(now.getMonthValue(), now.getYear());
        dashboard.setThisMonthRevenue(thisMonthRevenue != null ? thisMonthRevenue : BigDecimal.ZERO);

        Long overdueInvoices = invoiceRepository.countOverdueInvoices();
        dashboard.setOverdueInvoices(overdueInvoices != null ? overdueInvoices : 0L);

        Long activeContracts = contractRepository.countActiveContracts();
        dashboard.setActiveContracts(activeContracts != null ? activeContracts : 0L);

        dashboard.setAlerts(buildAlerts(
                overdueInvoices != null ? overdueInvoices : 0L,
                now.getMonthValue(),
                now.getYear()
        ));
        return dashboard;
    }

    private List<AdminAlertDTO> buildAlerts(Long overdueInvoices, int month, int year) {
        List<AdminAlertDTO> alerts = new ArrayList<>();

        if (overdueInvoices > 0) {
            alerts.add(new AdminAlertDTO(
                    "OVERDUE_INVOICE",
                    "Co " + overdueInvoices + " hoa don qua han can xu ly",
                    Math.toIntExact(overdueInvoices),
                    "ERROR"
            ));
        }

        long roomsWithoutInvoice = contractRepository.countRoomsWithoutInvoice(month, year);
        if (roomsWithoutInvoice > 0) {
            alerts.add(new AdminAlertDTO(
                    "MISSING_INVOICE",
                    "Co " + roomsWithoutInvoice + " phong chua co hoa don ky nay",
                    Math.toIntExact(roomsWithoutInvoice),
                    "WARNING"
            ));
        }

        long blockedHosts = userRepository.countByRole_RoleNameAndIsActiveFalse("HOST");
        if (blockedHosts > 0) {
            alerts.add(new AdminAlertDTO(
                    "HOST_BLOCKED",
                    "Co " + blockedHosts + " host dang bi khoa",
                    Math.toIntExact(blockedHosts),
                    "INFO"
            ));
        }

        return alerts;
    }
}
