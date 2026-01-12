package com.viet.aodai.auth.domain.dto;

import com.viet.aodai.auth.domain.enumeration.AuthStep;
import com.viet.aodai.auth.domain.enumeration.MfaType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PendingAuthSession {
    private UUID userId;
    private String username;
    private String deviceFingerprint;
    private AuthStep currentStep;
    private MfaType selectedMfaType;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean otpSent;
}
