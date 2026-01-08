package com.viet.aodai.auth.service;

import com.viet.aodai.auth.domain.enumration.AuthStep;
import com.viet.aodai.auth.domain.request.LoginRequest;
import com.viet.aodai.auth.domain.response.LoginResponse;
import com.viet.aodai.user.domain.entity.User;

public interface AuthStrategy {
    LoginResponse authenticate(LoginRequest request, User user);
    boolean support(AuthStep step);

}
