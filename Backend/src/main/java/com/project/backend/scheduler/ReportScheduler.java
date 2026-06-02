package com.project.backend.scheduler;

import com.project.backend.entity.User;
import com.project.backend.repository.UserRepository;
import com.project.backend.dto.host.report.ReportDTO;
import com.project.backend.service.host.HostEmailService;
import com.project.backend.service.host.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Vai trò: Scheduler của module host-service.
 * Chức năng: Thực thi các tác vụ nền liên quan đến report theo lịch.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReportScheduler {

    private final UserRepository userRepository;
    private final ReportService reportService;
    private final HostEmailService emailService;

        /**
     * Chức năng: Gửi monthly reports.
     */
@Scheduled(cron = "0 0 8 1 * ?")
    public void sendMonthlyReports() {
        List<User> hosts = userRepository.findByRole_RoleName("HOST");

        for (User host : hosts) {
            ReportDTO report = reportService.getDashboard(host.getUserId());
            if (host.getEmail() != null) {
                emailService.sendMonthlyReport(host.getEmail(), report);
                log.info("Gửi báo cáo tháng đến host: {}", host.getEmail());
            }
        }
    }
}
