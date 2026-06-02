package com.project.backend.service.admin;

import com.project.backend.dto.admin.revenue.AdminRevenueDTO;
import com.project.backend.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Vai trò: Service xử lý nghiệp vụ của module admin-service.
 * Chức năng: Chứa logic xử lý liên quan đến admin revenue.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminRevenueService {

    private final InvoiceRepository invoiceRepository;

        /**
     * Chức năng: Lấy dữ liệu revenue.
     */
public AdminRevenueDTO getRevenue(String period) {
        AdminRevenueDTO revenue = new AdminRevenueDTO();
        
        if ("month".equalsIgnoreCase(period)) {
            return getMonthlyRevenue();
        } else if ("quarter".equalsIgnoreCase(period)) {
            return getQuarterlyRevenue();
        } else if ("year".equalsIgnoreCase(period)) {
            return getYearlyRevenue();
        } else {
            log.warn("Invalid period: {}, defaulting to month", period);
            return getMonthlyRevenue();
        }
    }

        /**
     * Chức năng: Lấy dữ liệu monthly revenue.
     */
private AdminRevenueDTO getMonthlyRevenue() {
        AdminRevenueDTO revenue = new AdminRevenueDTO();
        Map<String, BigDecimal> revenueByMonth = new LinkedHashMap<>();

        BigDecimal totalRevenue = BigDecimal.ZERO;

        
        for (int i = 11; i >= 0; i--) {
            YearMonth month = YearMonth.now().minusMonths(i);
            BigDecimal monthRevenue = invoiceRepository.sumRevenueByPeriod(month.getMonthValue(), month.getYear());
            monthRevenue = monthRevenue != null ? monthRevenue : BigDecimal.ZERO;

            revenueByMonth.put(month.toString(), monthRevenue);
            totalRevenue = totalRevenue.add(monthRevenue);
        }

        BigDecimal averageRevenue = totalRevenue.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        revenue.setTotalRevenue(totalRevenue);
        revenue.setAverageRevenue(averageRevenue);
        revenue.setRevenueByPeriod(revenueByMonth);

        return revenue;
    }

        /**
     * Chức năng: Lấy dữ liệu quarterly revenue.
     */
private AdminRevenueDTO getQuarterlyRevenue() {
        AdminRevenueDTO revenue = new AdminRevenueDTO();
        Map<String, BigDecimal> revenueByQuarter = new LinkedHashMap<>();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        YearMonth now = YearMonth.now();

        
        for (int i = 3; i >= 0; i--) {
            YearMonth quarter = now.minusMonths(i * 3L);
            BigDecimal quarterRevenue = BigDecimal.ZERO;

            
            for (int month = 0; month < 3; month++) {
                YearMonth m = quarter.plusMonths(month);
                BigDecimal monthRevenue = invoiceRepository.sumRevenueByPeriod(m.getMonthValue(), m.getYear());
                quarterRevenue = quarterRevenue.add(monthRevenue != null ? monthRevenue : BigDecimal.ZERO);
            }

            String quarterLabel = String.format("Q%d/%d", (quarter.getMonthValue() - 1) / 3 + 1, quarter.getYear());
            revenueByQuarter.put(quarterLabel, quarterRevenue);
            totalRevenue = totalRevenue.add(quarterRevenue);
        }

        BigDecimal averageRevenue = totalRevenue.divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);

        revenue.setTotalRevenue(totalRevenue);
        revenue.setAverageRevenue(averageRevenue);
        revenue.setRevenueByPeriod(revenueByQuarter);

        return revenue;
    }

        /**
     * Chức năng: Lấy dữ liệu yearly revenue.
     */
private AdminRevenueDTO getYearlyRevenue() {
        AdminRevenueDTO revenue = new AdminRevenueDTO();
        Map<String, BigDecimal> revenueByYear = new LinkedHashMap<>();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        int currentYear = YearMonth.now().getYear();

        
        for (int i = 4; i >= 0; i--) {
            int year = currentYear - i;
            BigDecimal yearRevenue = BigDecimal.ZERO;

            
            for (int month = 1; month <= 12; month++) {
                BigDecimal monthRevenue = invoiceRepository.sumRevenueByPeriod(month, year);
                yearRevenue = yearRevenue.add(monthRevenue != null ? monthRevenue : BigDecimal.ZERO);
            }

            revenueByYear.put(String.valueOf(year), yearRevenue);
            totalRevenue = totalRevenue.add(yearRevenue);
        }

        BigDecimal averageRevenue = totalRevenue.divide(BigDecimal.valueOf(5), 2, RoundingMode.HALF_UP);

        revenue.setTotalRevenue(totalRevenue);
        revenue.setAverageRevenue(averageRevenue);
        revenue.setRevenueByPeriod(revenueByYear);

        return revenue;
    }
}
