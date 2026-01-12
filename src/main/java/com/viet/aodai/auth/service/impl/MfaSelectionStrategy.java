package com.viet.aodai.auth.service.impl;

import com.viet.aodai.auth.domain.dto.PendingAuthSession;
import com.viet.aodai.auth.domain.enumeration.AuthStep;
import com.viet.aodai.auth.domain.enumeration.MfaType;
import com.viet.aodai.auth.domain.request.SelectMfaRequest;

import com.viet.aodai.auth.domain.response.AuthResponse;
import com.viet.aodai.auth.domain.security.SessionService;
import com.viet.aodai.auth.service.AuthStrategy;
import com.viet.aodai.auth.service.MfaService;
import com.viet.aodai.core.common.exception.AuthException;
import com.viet.aodai.user.domain.entity.User;
import com.viet.aodai.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MfaSelectionStrategy implements AuthStrategy {
    private final UserRepository userRepository;

    private final SessionService sessionService;

    private final MfaService mfaService;

    @Override
    public AuthResponse execute(Object request) {
        SelectMfaRequest selectMfaRequest = (SelectMfaRequest) request;
        log.info("MFA verification for session: {}", selectMfaRequest.getSessionToken());

        // Validate session
        PendingAuthSession session = sessionService.getSession(selectMfaRequest.getSessionToken())
                .orElseThrow(()-> new AuthException("Invalid or expired session"));


        // Validate current step
        if (session.getCurrentStep() != AuthStep.MFA_REQUIRED){
            throw new AuthException("Invalid authentication step");
        }

        User user = userRepository.findUserById(session.getUserId())
                .orElseThrow(()-> new AuthException("User not found"));

        validateMfaType(user, ((SelectMfaRequest) request).getMfaType());

        mfaService.generateAndSendOtp(user, ((SelectMfaRequest) request).getMfaType());

        sessionService.updateSessionToken(
                ((SelectMfaRequest) request).getSessionToken(),
                AuthStep.MFA_VERIFY,
                ((SelectMfaRequest) request).getMfaType()
        );

        sessionService.markOtpSent(((SelectMfaRequest) request).getSessionToken());

        return AuthResponse.builder()
                .nextStep(AuthStep.MFA_VERIFY)
                .message("OTP has been sent to your" + ((SelectMfaRequest) request).getMfaType())
                .sessionToken(((SelectMfaRequest) request).getSessionToken())
                .mfaRequired(true)
                .build();
    }

    @Override
    public boolean supports(AuthStep step) {

        return step == AuthStep.MFA_REQUIRED;
    }

    @Override
    public AuthStep getStep() {

        return AuthStep.MFA_REQUIRED;
    }

    private void validateMfaType(User user, MfaType mfaType){
        switch (mfaType){
            case EMAIL:
                if (user.getEmail() == null || !user.isEmailVerified()){
                    throw  new AuthException("Email MFA not available");
                }
                break;
            case SMS:
                if (user.getPhoneNumber() == null || !user.isPhoneVerified()){
                    throw new AuthException("SMS MFA not available");
                }
                break;
        }
    }
}
