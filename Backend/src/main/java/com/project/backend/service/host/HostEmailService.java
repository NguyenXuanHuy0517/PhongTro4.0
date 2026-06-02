package com.project.backend.service.host;

import com.project.backend.dto.host.invoice.InvoiceResponseDTO;
import com.project.backend.dto.host.report.ReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Vai trò: Service xử lý nghiệp vụ của module host-service.
 * Chức năng: Chứa logic xử lý liên quan đến email.
 */
@Service
@RequiredArgsConstructor
public class HostEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

        /**
     * Chức năng: Gửi overdue email.
     */
public void sendOverdueEmail(String to, InvoiceResponseDTO invoice) {
        if (!mailEnabled) return;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Nhắc nhở thanh toán hóa đơn tháng " + invoice.getBillingMonth() + "/" + invoice.getBillingYear());
        message.setText("Bạn có hóa đơn chưa thanh toán: " + invoice.getInvoiceCode()
                + "\nTổng tiền: " + invoice.getTotalAmount()
                + "\nVui lòng thanh toán sớm để tránh phát sinh phí.");
        mailSender.send(message);
    }

        /**
     * Chức năng: Gửi monthly report.
     */
public void sendMonthlyReport(String to, ReportDTO report) {
        if (!mailEnabled) return;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Báo cáo doanh thu tháng");
        message.setText("Doanh thu tháng này: " + report.getTotalRevenue()
                + "\nTổng phòng: " + report.getTotalRooms()
                + "\nPhòng đang thuê: " + report.getRentedRooms()
                + "\nTỷ lệ lấp đầy: " + report.getOccupancyRate() + "%");
        mailSender.send(message);
    }
}
