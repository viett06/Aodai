package com.viet.aodai.auth.service.impl;


import com.viet.aodai.auth.domain.dto.PendingAuthSession;
import com.viet.aodai.auth.domain.enumeration.AuthStep;
import com.viet.aodai.auth.domain.enumeration.MfaType;
import com.viet.aodai.auth.domain.enumeration.TokenType;
import com.viet.aodai.auth.domain.request.LoginRequest;
import com.viet.aodai.auth.domain.request.SelectMfaRequest;
import com.viet.aodai.auth.domain.request.VerifyMfaRequest;
import com.viet.aodai.auth.domain.response.AuthResponse;
import com.viet.aodai.auth.domain.security.JwtTokenProvider;
import com.viet.aodai.auth.domain.security.SessionService;
import com.viet.aodai.auth.repository.InvalidateAccessTokenRepository;
import com.viet.aodai.auth.service.AuthService;
import com.viet.aodai.auth.service.AuthStrategy;
import com.viet.aodai.core.common.exception.*;
import com.viet.aodai.core.config.PasswordEncoderConfig;
import com.viet.aodai.user.domain.entity.User;
import com.viet.aodai.user.domain.enumeration.UserRole;
import com.viet.aodai.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    private final List<AuthStrategy> authStrategies;
    private Map<AuthStep, AuthStrategy> strategyMap;
    private final JwtTokenProvider jwtTokenProvider;
    private final InvalidateAccessTokenRepository invalidateAccessTokenRepository;
    private final PasswordEncoderConfig passwordEncoderConfig;
    private final SessionService sessionService;

    @PostConstruct
    // tra cứu map thông qua key là step ở các class implement AuthStrategy
    // Function.identity() trả về chính object đó
    public void init(){
        strategyMap = authStrategies.stream().collect(Collectors.toMap(
                AuthStrategy::getStep,
                Function.identity()
        ));
//        Map<AuthStep, AuthStrategy> map = new HashMap<>();
//
//        for (AuthStrategy strategy : authStrategies) {
//            AuthStep step = strategy.getStep();
//            map.put(step, strategy);
//        }
    }

    @Override
    public AuthResponse initialLogin(LoginRequest loginRequest) {
        log.info("Initial login attempt for username {}:", loginRequest.getUsername());

        AuthStrategy strategy = getStrategy(AuthStep.PASSWORD_VERIFY);
        return strategy.execute(loginRequest);
    }

    @Override
    @Transactional
    public AuthResponse selectMfaMethod(SelectMfaRequest request) {
        log.info("MFA method selection for session: {}", request.getSessionToken());

        AuthStrategy strategy = getStrategy(AuthStep.MFA_REQUIRED);
        return strategy.execute(request);
    }

    @Override
    @Transactional
    public AuthResponse verifyMfa(VerifyMfaRequest request) {
        log.info("MFA verification for session {}:", request.getSessionToken());

        AuthStrategy strategy = getStrategy(AuthStep.MFA_VERIFY);
        return strategy.execute(request);
    }

    // get strategy
    private AuthStrategy getStrategy(AuthStep authStep){
        AuthStrategy strategy = strategyMap.get(authStep);
        if (strategy ==null){
            throw new AuthException("No strategy found for step: " + authStep);

        }
        return strategy;
    }

    @Override
    public void logOut(String accessToken, String refreshToken) {
        try {

            jwtTokenProvider.validateToken(refreshToken);

            Claims refreshClaims = jwtTokenProvider.getClaimsFromToken(refreshToken);
            String userId = refreshClaims.getSubject();


            invalidateAccessTokenRepository.saveInvalidToken(refreshToken);

            if (accessToken != null && !accessToken.isEmpty()) {
                try {
                    jwtTokenProvider.validateToken(accessToken);
                    invalidateAccessTokenRepository.saveInvalidToken(accessToken);
                } catch (AuthException e) {
                    log.debug("Access token already invalid or expired");
                }
            }

            log.info("User {} logged out successfully", userId);

        } catch (AuthException e) {
            log.warn("Logout with invalid refresh token: {}", e.getMessage());
            //cố gắng invalidate
            invalidateAccessTokenRepository.saveInvalidToken(refreshToken);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public String getAccessToken(String refreshToken) {
        // Validate token
        jwtTokenProvider.validateToken(refreshToken);

        // Check blacklist
        if (invalidateAccessTokenRepository.isTokenInvalidated(refreshToken)) {
            throw new AuthException("Refresh token has been revoked");
        }

        Claims claims = jwtTokenProvider.getClaimsFromToken(refreshToken);

        String tokenType = claims.get("type", String.class);
        if (!TokenType.REFRESH.name().equals(tokenType)) {
            throw new AuthException("Invalid token type");
        }

        String role = claims.get("role", String.class);
        if (!UserRole.SYSTEM.name().equals(role)) {
            throw new AuthException("Invalid token role. Only refresh tokens allowed");
        }

        UUID userId = claims.get("userId", UUID.class);
        if (userId == null) {
            throw new AuthException("User ID not found in token");
        }

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new AuthException("User not found"));

        return jwtTokenProvider.generateAccessToken(user);
    }

    @Override
    public AuthResponse initiateForgotPassword(String email) {
        User user = userRepository.findUserByUsername(email)
                .orElseThrow(() -> new AuthException("User not found"));

        if (!user.isAccountNonLocked()) {
            throw new AuthException("Account is locked");
        }



        String sessionToken = sessionService.generateSessionToken(
                user.getUserId(),
                user.getUsername()
        );

        sessionService.updateSessionToken(sessionToken, AuthStep.MFA_REQUIRED, null);

        return AuthResponse.builder()
                .nextStep(AuthStep.MFA_REQUIRED)
                .message("Please select MFA method")
                .sessionToken(sessionToken)
                .mfaRequired(true)
                .availableMfaTypes(getAvailableAttempts(user))
                .build();

    }

    @Override
    @Transactional
    public AuthResponse SelectMfaForgotPassword(String sessionToken, MfaType type){

        SelectMfaRequest selectMfaRequest = SelectMfaRequest.builder()
                .sessionToken(sessionToken)
                .mfaType(type)
                .build();

        return selectMfaMethod(selectMfaRequest);

    }

    @Override
    @Transactional
    public AuthResponse completeForgotPassword(String sessionToken, String newPassword, String otpCode) {
        // Verify OTP
        VerifyMfaRequest verifyRequest = VerifyMfaRequest.builder()
                .sessionToken(sessionToken)
                .otpCode(otpCode)
                .isForgetPassword(true)
                .build();

        AuthResponse otpResponse = verifyMfa(verifyRequest);

        if (otpResponse.getNextStep() != AuthStep.COMPLETE) {
            throw new AuthException("OTP verification failed");
        }

        // Lấy userId từ session
        PendingAuthSession session = sessionService.getSession(sessionToken)
                .orElseThrow(() -> new AuthException("Invalid session"));

        // Update password
        String hashedPassword = passwordEncoderConfig.passwordEncoder().encode(newPassword);
        userRepository.updatePassword(session.getUserId(), hashedPassword);

        // Cleanup session
        sessionService.invalidateSession(sessionToken);

        log.info("Password reset successfully for user: {}", session.getUserId());

        return AuthResponse.builder()
                .nextStep(AuthStep.COMPLETE)
                .message("Password has been reset successfully")
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
}