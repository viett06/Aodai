package com.viet.aodai.auth.domain.response;

import com.viet.aodai.auth.domain.enumration.AuthStep;
import com.viet.aodai.auth.domain.enumration.MfaType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private AuthStep nextStep;
    private String message;
    private boolean mfaRequired;
    private MfaType mfaType;
}
