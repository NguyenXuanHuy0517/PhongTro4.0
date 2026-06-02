package com.project.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String role;
    private String token;
    private boolean requiresRentalJoin;
}
