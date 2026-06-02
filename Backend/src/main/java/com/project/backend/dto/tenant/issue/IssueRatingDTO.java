package com.project.backend.dto.tenant.issue;

import lombok.Data;

/**
 * Vai trò: DTO của module tenant-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến issue rating để trao đổi giữa các tầng.
 */
@Data
public class IssueRatingDTO {
    private Integer rating;       
    private String tenantFeedback;
}
