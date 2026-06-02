package com.project.backend.dto.host.area;

import lombok.Data;

/**
 * Vai trò: DTO của module host-service.
 * Chức năng: Đóng gói dữ liệu liên quan đến area create để trao đổi giữa các tầng.
 */
@Data
public class AreaCreateDTO {
    private String areaName;
    private String address;
    private String ward;
    private String district;
    private String city;
    private Double latitude;
    private Double longitude;
    private String description;
}
