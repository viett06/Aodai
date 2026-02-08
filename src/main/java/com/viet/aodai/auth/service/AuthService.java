package com.viet.aodai.auth.service;

import com.viet.aodai.auth.domain.enumeration.MfaType;
import com.viet.aodai.auth.domain.request.LoginRequest;
import com.viet.aodai.auth.domain.request.SelectMfaRequest;
import com.viet.aodai.auth.domain.request.VerifyMfaRequest;
import com.viet.aodai.auth.domain.response.AuthResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    AuthResponse initialLogin(LoginRequest loginRequest);

    AuthResponse selectMfaMethod(SelectMfaRequest request);

    AuthResponse verifyMfa(VerifyMfaRequest request);

    void logOut(String token, String refreshToken);

    String getAccessToken(String refreshToken);

    AuthResponse initiateForgotPassword(String email);

    AuthResponse completeForgotPassword(String sessionToken, String newPassword, String otpCode);

    AuthResponse SelectMfaForgotPassword(String sessionToken, MfaType type);
}
