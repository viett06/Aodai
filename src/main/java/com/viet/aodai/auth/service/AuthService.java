package com.viet.aodai.auth.service;

import com.viet.aodai.auth.domain.request.LoginRequest;
import com.viet.aodai.auth.domain.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    LoginResponse login(LoginRequest request, HttpServletRequest httpServletRequest);
}
