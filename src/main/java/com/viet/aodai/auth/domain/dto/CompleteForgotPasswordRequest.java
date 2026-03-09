package com.viet.aodai.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompleteForgotPasswordRequest {
    private String sessionToken;
    private String newPassword;
    private String otpCode;
}
