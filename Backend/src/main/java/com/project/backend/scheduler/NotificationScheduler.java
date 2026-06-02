package com.project.backend.scheduler;

import com.project.backend.entity.Contract;
import com.project.backend.entity.Invoice;
import com.project.backend.repository.ContractRepository;
import com.project.backend.repository.InvoiceRepository;
import com.project.backend.mapper.host.InvoiceMapper;
import com.project.backend.service.host.HostEmailService;
import com.project.backend.service.host.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Vai trò: Scheduler của module host-service.
 * Chức năng: Thực thi các tác vụ nền liên quan đến notification theo lịch.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;
    private final NotificationService notificationService;
    private final HostEmailService emailService;
    private final InvoiceMapper invoiceMapper;

        /**
     * Chức năng: Gửi overdue reminders.
     */
@Scheduled(cron = "0 0 9 * * ?")
    @Transactional
    public void sendOverdueReminders() {
        List<Invoice> overdueInvoices = invoiceRepository.findByStatusIn(List.of("UNPAID", "OVERDUE"));

        for (Invoice invoice : overdueInvoices) {
            if (invoice.getDueDate() != null && invoice.getDueDate().isBefore(LocalDate.now())) {
                invoice.setStatus("OVERDUE");
                invoiceRepository.save(invoice);
            }

            Long tenantId = invoice.getContract().getTenant().getUserId();
            notificationService.sendToUser(
                    tenantId,
                    "INVOICE_OVERDUE",
                    "Nhắc nhở thanh toán",
                    "Hóa đơn " + invoice.getInvoiceCode() + " chưa được thanh toán",
                    "INVOICE",
                    invoice.getInvoiceId()
            );

            String tenantEmail = invoice.getContract().getTenant().getEmail();
            if (tenantEmail != null) {
                emailService.sendOverdueEmail(tenantEmail, invoiceMapper.toResponseDTO(invoice));
            }

            log.info("Gửi nhắc hóa đơn: {} đến tenant: {}", invoice.getInvoiceCode(), tenantId);
        }
    }

        /**
     * Chức năng: Thực hiện nghiệp vụ remind expiring contracts.
     */
@Scheduled(cron = "0 30 9 * * ?")
    @Transactional
    public void remindExpiringContracts() {
        LocalDate now = LocalDate.now();
        LocalDate in30Days = now.plusDays(30);
        LocalDate in7Days = now.plusDays(7);

        List<Contract> expiringContracts = contractRepository
                .findByStatusAndEndDateBefore("ACTIVE", in30Days);

        for (Contract contract : expiringContracts) {
            Long tenantId = contract.getTenant().getUserId();
            long daysLeft = contract.getEndDate().toEpochDay() - now.toEpochDay();

            notificationService.sendToUser(
                    tenantId,
                    "CONTRACT_EXPIRING",
                    "Hợp đồng sắp hết hạn",
                    "Hợp đồng của bạn còn " + daysLeft + " ngày nữa sẽ hết hạn",
                    "CONTRACT",
                    contract.getContractId()
            );

            log.info("Nhắc hợp đồng: {} còn {} ngày", contract.getContractCode(), daysLeft);
        }
    }
}
