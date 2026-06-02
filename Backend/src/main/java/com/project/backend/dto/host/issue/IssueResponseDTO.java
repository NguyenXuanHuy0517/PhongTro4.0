package com.project.backend.dto.host.issue;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến issue để trao đổi giữa các tầng.
 */
@Data
public class IssueResponseDTO {
    private Long issueId;
    private String title;
    private String description;
    private String tenantName;
    private String roomCode;
    private String priority;
    private String status;
    private String handlerNote;
    private Integer rating;
    private String tenantFeedback;
    private LocalDateTime createdAt;

    
    private String issueType;
    private String areaName;
    private Long areaId;
    private String suggestedServiceName;
    private String suggestionNote;
}
