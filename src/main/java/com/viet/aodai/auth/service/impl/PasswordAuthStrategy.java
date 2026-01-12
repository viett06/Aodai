package com.viet.aodai.auth.service.impl;

import com.viet.aodai.auth.domain.enumeration.AuthStep;
import com.viet.aodai.auth.domain.enumeration.MfaType;
import com.viet.aodai.auth.domain.request.LoginRequest;
import com.viet.aodai.auth.domain.response.AuthResponse;
import com.viet.aodai.auth.domain.security.JwtTokenProvider;
import com.viet.aodai.auth.domain.security.SessionService;
import com.viet.aodai.auth.service.AuthStrategy;
import com.viet.aodai.auth.service.MfaService;
import com.viet.aodai.core.common.exception.AuthException;
import com.viet.aodai.core.common.exception.PassWordErrorException;
import com.viet.aodai.core.config.SecurityConfig;
import com.viet.aodai.user.domain.dto.UserStatus;
import com.viet.aodai.user.domain.entity.User;
import com.viet.aodai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordAuthStrategy implements AuthStrategy {
    private final UserRepository userRepository;
    private final SecurityConfig securityConfig;
    private final SessionService sessionService;
    private final MfaService mfaService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse execute(Object request) {
        LoginRequest loginRequest = (LoginRequest) request;
        log.info("PassWord authentication for username: {}", loginRequest.getUsername());

        User user = userRepository.findUserByUsername(loginRequest.getUsername())
                .orElseThrow(()-> new AuthException("Invalid credentials"));

        validateAccountStatus(user);

        if (!securityConfig.passwordEncoder().matches(loginRequest.getPassword(),user.getPasswordHash())){
            handleFailedLogin(user);
            throw new PassWordErrorException("Invalid credentials");
        }

        user.setLastLogin(LocalDateTime.now());
        resetFailedAttempts(user);

//        String sessionToken = sessionService.generateSessionToken(
//                user.getUserId(),
//                user.getUsername(),
//                loginRequest.getDeviceFingerprint()
//        );
//
//        if (user.isMfaEnabled()){
//            return AuthResponse.builder()
//                    .nextStep(AuthStep.MFA_REQUIRED)
//                    .sessionToken(sessionToken)
//                    .sessionToken(sessionToken)
//                    .mfaRequired(true)
//                    .availableMfaTypes(getAvailableAttempts(user))
//                    .build();
//        }
//
//        log.warn("User {} has MFA disabled - skipping MFA", user.getUsername());
//
//        sessionService.invalidateSession(sessionToken);
//
//        return AuthResponse.builder()
//                .nextStep(AuthStep.COMPLETE)
//                .message("Login successful")
//                .mfaRequired(false)
//                .build();

        if (user.isMfaEnabled()) {
            return handleMfaEnabledUser(user, loginRequest.getDeviceFingerprint());
        } else {
            return handleMfaDisabledUser(user, loginRequest.getDeviceFingerprint());
        }
    }

    @Override
    public boolean supports(AuthStep step) {
        return step ==AuthStep.PASSWORD_VERIFY;
    }

    @Override
    public AuthStep getStep() {
        return AuthStep.PASSWORD_VERIFY;
    }

    private boolean validateAccountStatus(User user){
        return user.getStatus().equals(UserStatus.ACTIVE);
    }

    private void handleFailedLogin(User user){
        user.incrementFailedAttempt();
        userRepository.save(user);
    }

    private void resetFailedAttempts(User user){
        user.resetFailedAttempts();
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
    private AuthResponse handMfaRequired(User user, MfaType mfaType){
        // Send OTP to user's preferred method
        mfaService.generateAndSendOtp(user, mfaType);
        return AuthResponse.builder()
                .nextStep(AuthStep.MFA_REQUIRED)
                .message("MFA code has been sent to your registered email/phone")
                .mfaRequired(true)
                .build();
    }

    private List<MfaType> getAvailableAttempts(User user){
        List<MfaType> types = new ArrayList<>();
        if (user.getEmail() !=  null && user.isEmailVerified()){
            types.add(MfaType.EMAIL);
        }
        if (user.getPhoneNumber() != null && user.isPhoneVerified()){
            types.add(MfaType.SMS);
        }
        if (types.isEmpty()){
            log.error("User {} has MFA enabled but no verified contact methods!",
                    user.getUsername());
            throw new AuthException("No verified MFA methods available. Please contact support.");
        }
        return types;
    }

    private AuthResponse handleMfaDisabledUser(User user, String deviceFingerprint){
        log.warn("User {} has MFA DISABLED - generating tokens directly (security risk!)",
                user.getUsername());
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user, deviceFingerprint);

        return AuthResponse.builder()
                .nextStep(AuthStep.COMPLETE)
                .message("Login successful")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .mfaRequired(false)
                .build();
    }

    private AuthResponse handleMfaEnabledUser(User user, String deviceFingerprint){
        log.info("User {} has MFA enabled - proceeding to MFA selection", user.getUsername());

        String sessionToken = sessionService.generateSessionToken(
                user.getUserId(),
                user.getUsername(),
                deviceFingerprint
        );

        return AuthResponse.builder()
                .nextStep(AuthStep.MFA_REQUIRED)
                .message("Please select MFA method")
                .sessionToken(sessionToken)
                .mfaRequired(true)
                .availableMfaTypes(getAvailableAttempts(user))
                .build();
    }



}
