package com.viet.aodai.auth.service.impl;

import com.twilio.rest.api.v2010.account.availablephonenumbercountry.Local;
import com.viet.aodai.auth.domain.enumration.AuthStep;
import com.viet.aodai.auth.domain.enumration.MfaType;
import com.viet.aodai.auth.domain.request.LoginRequest;
import com.viet.aodai.auth.domain.response.LoginResponse;
import com.viet.aodai.auth.domain.security.SessionService;
import com.viet.aodai.auth.service.AuthStrategy;
import com.viet.aodai.auth.service.MfaService;
import com.viet.aodai.core.common.exception.AccountIsLocked;
import com.viet.aodai.core.common.exception.PassWordErrorException;
import com.viet.aodai.core.common.exception.StatusUserException;
import com.viet.aodai.core.config.SecurityConfig;
import com.viet.aodai.user.domain.entity.User;
import com.viet.aodai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordAuthStrategy implements AuthStrategy {
    private final UserRepository userRepository;
    private final SecurityConfig securityConfig;
    private final SessionService sessionService;
    private final MfaService mfaService;

    @Override
    public LoginResponse authenticate(LoginRequest request, User user) {
        log.info("Password authentication for user: {}", user.getUsername());

        // check account status
        validateAccountStatus(user);

        //verify Password
        if(!securityConfig.passwordEncoder().matches(request.getPassword(),user.getPasswordHash())){
            handleFailedLogin(user);
            throw new PassWordErrorException("Invalid credentials");
        }

        //reset password failed
        resetFailedAttempts(user);

        // Generate temporary session token
        String sessionToken = sessionService.createSession(user.getUserId());

        // check if mfa required
        if (user.isMfaEnabled()) {
            return handMfaRequired(user, request.getMfaType());
        }
        return LoginResponse.builder()
                .nextStep(AuthStep.FAILED)
                .mfaRequired(false)
                .build();
    }

    @Override
    public boolean support(AuthStep step) {
        return false;
    }
    private void validateAccountStatus(User user){
        if (!user.isAccountNonLocked()){
            throw new AccountIsLocked("Account is locked. Try again later");
        }
        if (!user.isEnable()){
            throw new StatusUserException("Account is not active");
        }
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
    private LoginResponse handMfaRequired(User user, MfaType mfaType){
        // Send OTP to user's preferred method
        mfaService.generateAndSendOtp(user, mfaType);
        return LoginResponse.builder()
                .nextStep(AuthStep.MFA_REQUIRED)
                .message("MFA code has been sent to your registered email/phone")
                .mfaRequired(true)
                .build();
    }
//    private LoginResponse completeAuthentication(User user, String sessionToken) {
//        String accessToken = jwtTokenProvider.generateAccessToken(user);
//        String refreshToken = jwtTokenProvider.generateRefreshToken(user, sessionToken);
//
//        return LoginResponse.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .nextStep(AuthStep.COMPLETE)
//                .mfaRequired(false)
//                .build();
//    }
}
