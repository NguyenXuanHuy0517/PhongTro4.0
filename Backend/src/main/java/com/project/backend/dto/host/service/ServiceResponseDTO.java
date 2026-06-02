package com.project.backend.dto.host.service;

import lombok.Data;
import java.math.BigDecimal;

/**
 * Vai trò: Service xử lý nghiệp vụ của module host-service.
 * Chức năng: Chứa logic xử lý liên quan đến service response dto.
 */
@Data
public class ServiceResponseDTO {
    private Long serviceId;
    private String serviceName;
    private BigDecimal price;
    private String unitName;
    private String description;
    private boolean active;
    private int usageCount;
}
