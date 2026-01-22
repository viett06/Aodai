package com.viet.aodai.auth.service;

import com.viet.aodai.auth.domain.enumeration.AuthStep;
import com.viet.aodai.auth.domain.response.AuthResponse;

public interface AuthStrategy {
    AuthResponse execute(Object request);

    boolean supports(AuthStep step);

    AuthStep getStep();

}
