package com.project.backend.dto.host.issue;

import lombok.Data;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến issue status update để trao đổi giữa các tầng.
 */
@Data
public class IssueStatusUpdateDTO {
    private String status;
    private String handlerNote;
}
