package com.project.backend.dto.host.deposit;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến deposit create để trao đổi giữa các tầng.
 */
@Data
public class DepositCreateDTO {
    private Long tenantId;
    private Long roomId;
    private BigDecimal amount;
    private LocalDate expectedCheckIn;
    private String note;
}
