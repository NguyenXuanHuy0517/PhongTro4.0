package com.project.backend.dto.admin.host;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vai trò: DTO của module admin-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến admin host status update request để trao đổi giữa các tầng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminHostStatusUpdateRequest {
    private boolean active;          
    private String reason;           
    private String note;             
}
