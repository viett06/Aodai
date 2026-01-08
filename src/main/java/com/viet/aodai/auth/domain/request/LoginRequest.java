package com.viet.aodai.auth.domain.request;

import com.viet.aodai.auth.domain.enumration.MfaType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    private String username;
    private String password;
    private String deviceFingerprint;
    private MfaType mfaType;
    private String mfaCode;

}
