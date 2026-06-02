package com.project.backend.scheduler;

import com.project.backend.entity.Contract;
import com.project.backend.entity.Invoice;
import com.project.backend.repository.ContractRepository;
import com.project.backend.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceScheduler {

    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;

    @Scheduled(cron = "0 0 1 1 * ?")
    @Transactional
    public void createMonthlyInvoices() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        log.info("=== InvoiceScheduler: Bat dau tao hoa don thang {}/{} ===", month, year);

        List<Contract> activeContracts = contractRepository.findWithRelationsByStatus("ACTIVE");
        List<Long> contractIds = activeContracts.stream()
                .map(Contract::getContractId)
                .toList();
        Set<Long> existingContractIds = new HashSet<>(invoiceRepository
                .findExistingContractIdsForPeriod(contractIds, month, year));

        List<Invoice> invoicesToCreate = new ArrayList<>();
        int skipped = 0;

        for (Contract contract : activeContracts) {
            if (existingContractIds.contains(contract.getContractId())) {
                skipped++;
                continue;
            }

            Invoice invoice = new Invoice();
            invoice.setContract(contract);
            invoice.setInvoiceCode(String.format("INV-%d-%d%02d",
                    contract.getContractId(), year, month));
            invoice.setBillingMonth(month);
            invoice.setBillingYear(year);
            invoice.setDueDate(LocalDate.of(year, month, 15));
            invoice.setRentAmount(contract.getActualRentPrice());
            invoice.setStatus("UNPAID");
            invoicesToCreate.add(invoice);
        }

        if (!invoicesToCreate.isEmpty()) {
            invoiceRepository.saveAll(invoicesToCreate);
        }

        log.info("=== InvoiceScheduler hoan thanh: tao moi {}, bo qua {} ===",
                invoicesToCreate.size(), skipped);
    }
}
