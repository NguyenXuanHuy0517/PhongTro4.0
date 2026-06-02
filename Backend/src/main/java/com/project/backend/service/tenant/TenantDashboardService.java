package com.project.backend.service.tenant;

import com.project.backend.entity.Contract;
import com.project.backend.repository.ContractRepository;
import com.project.backend.repository.ContractServiceRepository;
import com.project.backend.repository.InvoiceRepository;
import com.project.backend.repository.IssueRepository;
import com.project.backend.repository.NotificationRepository;
import com.project.backend.dto.tenant.contract.MyContractDTO;
import com.project.backend.dto.tenant.dashboard.TenantDashboardSummaryDTO;
import com.project.backend.mapper.tenant.ContractMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantDashboardService {

    private final ContractRepository contractRepository;
    private final ContractServiceRepository contractServiceRepository;
    private final InvoiceRepository invoiceRepository;
    private final IssueRepository issueRepository;
    private final NotificationRepository notificationRepository;
    private final ContractMapper contractMapper;

    public TenantDashboardSummaryDTO getSummary(Long userId) {
        TenantDashboardSummaryDTO summary = new TenantDashboardSummaryDTO();
        contractRepository.findFirstByTenant_UserIdAndStatusOrderByStartDateDesc(userId, "ACTIVE")
                .ifPresent(contract -> summary.setCurrentContract(toContractDto(contract)));
        summary.setUnpaidCount(invoiceRepository.countByContract_Tenant_UserIdAndStatus(userId, "UNPAID"));
        summary.setOverdueCount(invoiceRepository.countByContract_Tenant_UserIdAndStatus(userId, "OVERDUE"));
        summary.setOpenIssueCount(issueRepository.countByTenant_UserIdAndStatusIn(userId, List.of("OPEN", "PROCESSING")));
        summary.setUnreadCount(notificationRepository.countByUser_UserIdAndIsReadFalse(userId));
        return summary;
    }

    private MyContractDTO toContractDto(Contract contract) {
        return contractMapper.toDTO(
                contract,
                contractServiceRepository.findByContract_ContractId(contract.getContractId())
        );
    }
}
