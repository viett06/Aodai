package com.viet.aodai.auth.service.impl;


import com.viet.aodai.auth.domain.dto.PendingAuthSession;
import com.viet.aodai.auth.domain.enumration.AuthStep;
import com.viet.aodai.auth.domain.request.VerifyMfaRequest;
import com.viet.aodai.auth.domain.response.AuthResponse;
import com.viet.aodai.auth.domain.security.JwtTokenProvider;
import com.viet.aodai.auth.domain.security.SessionService;
import com.viet.aodai.auth.service.AuthStrategy;
import com.viet.aodai.auth.service.MfaService;
import com.viet.aodai.core.common.exception.AuthException;
import com.viet.aodai.user.domain.entity.User;
import com.viet.aodai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Component
@Slf4j
public class MfaVerificationStrategy implements AuthStrategy {

    private final SessionService sessionService;
    private final MfaService mfaService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse execute(Object request) {
        VerifyMfaRequest verifyMfaRequest = (VerifyMfaRequest) request;

        log.info("MFA verification for session: {}", verifyMfaRequest.getSessionToken());

        // validate session
        PendingAuthSession session = sessionService.getSession(verifyMfaRequest.getSessionToken())
                .orElseThrow(()-> new AuthException("Invalid or expired session"));

        if (session.getCurrentStep() != AuthStep.MFA_VERIFY){
            throw  new AuthException("Invalid authentication step");
        }

        if (!session.isOtpSent()){
            throw new AuthException("No OTP was sent");
        }

        User user = userRepository.findUserById(session.getUserId())
                .orElseThrow(()-> new AuthException("User not found"));

        boolean isValid = mfaService.verifyOtp(user.getUserId(), verifyMfaRequest.getOtpCode());

        if (!isValid){
            throw new AuthException("Invalid or expired OTP");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user,session.getDeviceFingerprint());

        // cleanup session
        sessionService.invalidateSession(verifyMfaRequest.getSessionToken());

        log.info("User {} successful authenticated", user.getUsername());

        return AuthResponse.builder()
                .nextStep(AuthStep.COMPLETE)
                .message("Login successful")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .mfaRequired(false)
                .build();


    }

    @Override
    public boolean supports(AuthStep step) {
        return step == AuthStep.MFA_VERIFY;
    }

    @Override
    public AuthStep getStep() {
        return AuthStep.MFA_VERIFY;
    }
}
