package com.viet.aodai.auth.domain.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyMfaRequest {
    private String username;
    private String mfaCode;
    private String deviceFingerprint;
    private String sessionToken;
}
