package com.viet.aodai.auth.service;

import com.viet.aodai.auth.domain.enumration.AuthStep;
import com.viet.aodai.auth.domain.request.LoginRequest;
import com.viet.aodai.auth.domain.response.AuthResponse;
import com.viet.aodai.user.domain.entity.User;

public interface AuthStrategy {
    AuthResponse execute(Object request);

    boolean supports(AuthStep step);

    AuthStep getStep();
}
