package com.viet.aodai.auth.domain.request;

import com.viet.aodai.auth.domain.enumration.MfaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SelectMfaRequest {
    @NotBlank(message = "Session token is required")
    private String sessionToken;

    @NotNull(message = "MFA type is required")
    private MfaType mfaType;
}

