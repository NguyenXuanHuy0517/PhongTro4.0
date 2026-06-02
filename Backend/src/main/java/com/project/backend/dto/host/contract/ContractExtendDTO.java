package com.project.backend.dto.host.contract;

import lombok.Data;
import java.time.LocalDate;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến contract extend để trao đổi giữa các tầng.
 */
@Data
public class ContractExtendDTO {
    private LocalDate newEndDate;
}
