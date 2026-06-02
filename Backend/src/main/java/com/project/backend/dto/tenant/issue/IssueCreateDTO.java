package com.project.backend.dto.tenant.issue;

import lombok.Data;

/**
 * Vai trò: DTO của module tenant-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến issue create để trao đổi giữa các tầng.
 */
@Data
public class IssueCreateDTO {
    private String title;
    private String description;
    private String images;   
    private String priority; 

    
    private String issueType = "GENERAL";  

    
    private String suggestedServiceName;
    private String suggestionNote;
}
