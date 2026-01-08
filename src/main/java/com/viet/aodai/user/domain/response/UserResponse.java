package com.viet.aodai.user.domain.response;

import com.viet.aodai.user.domain.dto.UserRole;
import com.viet.aodai.user.domain.dto.UserStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private UUID userId;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
}
