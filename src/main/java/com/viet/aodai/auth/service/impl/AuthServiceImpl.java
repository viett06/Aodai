package com.viet.aodai.auth.service.impl;

import com.viet.aodai.auth.domain.enumration.AuthStep;
import com.viet.aodai.auth.domain.request.LoginRequest;
import com.viet.aodai.auth.domain.response.LoginResponse;
import com.viet.aodai.auth.domain.security.JwtTokenProvider;
import com.viet.aodai.auth.service.AuthService;
import com.viet.aodai.auth.service.AuthStrategy;
import com.viet.aodai.auth.service.MfaService;
import com.viet.aodai.core.common.exception.*;
import com.viet.aodai.user.domain.entity.User;
import com.viet.aodai.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;


@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MfaService mfaService;
    private final AuthStrategy authStrategy;

    @Override
    public LoginResponse login(LoginRequest request, HttpServletRequest httpServletRequest) {
        log.info("Login attempt for: {}", request.getUsername());

        try {
            //find user
            User user = userRepository.findUserByUsername(request.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
//            // check account status
//            if (!user.isAccountNonLocked()){
//                throw new AccountIsLocked("Account is locked. Try again later");
//
//            }
//
//            if (!user.isEnable()){
//                throw new AccountIsLocked("Account it not active");
//            }
//
//            if (!passwordEncoder.matches(request.getPassword(),user.getPasswordHash())){
//                user.incrementFailedAttempt();
//                userRepository.save(user);
//
//                log.warn("Failed login attempt for user: {}", user.getUserId());
//                throw new PassWordErrorException("Invalid credential");
//            }
//
//            // Reset failed attempts on successful login
//            user.resetFailedAttempts();
//            user.setLastLogin(LocalDateTime.now());
//            user.setMfaEnabled(true);
//            userRepository.save(user);
//
//
//            //Check MFA if enabled
//            if (user.isMfaEnabled()){
//
//
//            }
            authStrategy.authenticate(request,user);

            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user, request.getDeviceFingerprint());

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .message("login complete")
                    .nextStep(AuthStep.COMPLETE)
                    .build();
            
        }
        catch (Exception ex){
            log.error("login error", ex);
            throw  new AuthException("Login failed");
        }
    }

}