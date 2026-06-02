package com.project.backend.dto.tenant.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Vai trò: Service xử lý nghiệp vụ của module tenant-service.
 * Chức năng: Chứa logic xử lý liên quan đến add service to contract dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddServiceToContractDTO {
    @NotNull(message = "Hợp đồng không được để trống")
    private Long contractId;

    @NotNull(message = "Dịch vụ không được để trống")
    private Long serviceId;

    @Positive(message = "Số lượng phải lớn hơn 0")
    private Integer quantity = 1;
}
