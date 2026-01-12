package com.viet.aodai.auth.domain.response;

import com.viet.aodai.auth.domain.enumeration.AuthStep;
import com.viet.aodai.auth.domain.enumeration.MfaType;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuthResponse {
    private AuthStep nextStep;
    private String message;
    private String sessionToken;  // Dùng để track giữa các bước
    private String accessToken;   // Chỉ có khi COMPLETE
    private String refreshToken;  // Chỉ có khi COMPLETE
    private boolean mfaRequired;
    private List<MfaType> availableMfaTypes;  // Các phương thức MFA có sẵn
}
