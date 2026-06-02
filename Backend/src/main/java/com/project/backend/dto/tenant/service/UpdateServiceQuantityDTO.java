package com.project.backend.dto.tenant.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Positive;

/**
 * Vai trò: Service xử lý nghiệp vụ của module tenant-service.
 * Chức năng: Chứa logic xử lý liên quan đến update service quantity dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateServiceQuantityDTO {
    @Positive(message = "Số lượng phải lớn hơn 0")
    private Integer quantity;
}
