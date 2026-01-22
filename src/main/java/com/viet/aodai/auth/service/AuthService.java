package com.viet.aodai.auth.service;

import com.viet.aodai.auth.domain.request.LoginRequest;
import com.viet.aodai.auth.domain.request.SelectMfaRequest;
import com.viet.aodai.auth.domain.request.VerifyMfaRequest;
import com.viet.aodai.auth.domain.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    AuthResponse initialLogin(LoginRequest loginRequest);

    AuthResponse selectMfaMethod(SelectMfaRequest request);

    AuthResponse verifyMfa(VerifyMfaRequest request);

    void logOut(String tôken, String refreshToken);
}
