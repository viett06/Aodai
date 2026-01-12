package com.viet.aodai.auth.domain.dto;

import com.viet.aodai.auth.domain.enumration.AuthStep;
import com.viet.aodai.auth.domain.enumration.MfaType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

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
