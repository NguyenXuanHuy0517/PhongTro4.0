package com.project.backend.dto.tenant.dashboard;

import com.project.backend.dto.tenant.contract.MyContractDTO;
import lombok.Data;

@Data
public class TenantDashboardSummaryDTO {
    private MyContractDTO currentContract;
    private long unpaidCount;
    private long overdueCount;
    private long openIssueCount;
    private long unreadCount;
}
