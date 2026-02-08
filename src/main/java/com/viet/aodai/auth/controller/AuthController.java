package com.viet.aodai.auth.controller;

import com.viet.aodai.auth.domain.request.LoginRequest;
import com.viet.aodai.auth.domain.request.SelectMfaRequest;
import com.viet.aodai.auth.domain.request.VerifyMfaRequest;
import com.viet.aodai.auth.domain.response.AuthResponse;
import com.viet.aodai.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        log.info("POST /api/v1/auth/login - username: {}", loginRequest.getUsername());
        AuthResponse response = authService.initialLogin(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mfa/select")
    public ResponseEntity<AuthResponse> selectMfaMethod(@Valid @RequestBody SelectMfaRequest selectMfaRequest){
        log.info("POST /api/v1/mfa/select - session: {},type: {}",selectMfaRequest.getSessionToken(), selectMfaRequest.getMfaType());

        AuthResponse response = authService.selectMfaMethod(selectMfaRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<AuthResponse> verifyMfa(
            @Valid @RequestBody VerifyMfaRequest request
    ) {
        log.info("POST /api/v1/auth/mfa/verify - session: {}", request.getSessionToken());
        AuthResponse response = authService.verifyMfa(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password/initiate")
    public ResponseEntity<AuthResponse> initiateForgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.initiateForgotPassword(request.getEmail()));
    }

    @PostMapping("/forgot-password/select-mfa")
    public ResponseEntity<AuthResponse> selectMfaForForgotPassword(@RequestBody SelectMfaRequest request) {
        return ResponseEntity.ok(authService.selectMfaMethod(request));
    }

    @PostMapping("/forgot-password/complete")
    public ResponseEntity<AuthResponse> completeForgotPassword(@RequestBody CompleteForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.completeForgotPassword(
                request.getSessionToken(),
                request.getNewPassword(),
                request.getOtpCode()
        ));
    }

}
