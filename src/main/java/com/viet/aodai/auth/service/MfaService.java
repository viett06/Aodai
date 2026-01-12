package com.viet.aodai.auth.service;

import com.viet.aodai.auth.domain.enumeration.MfaType;
import com.viet.aodai.user.domain.entity.User;

import java.util.UUID;

public interface MfaService {
    void generateAndSendOtp(User user, MfaType mfaType);

    boolean verifyOtp(UUID userId, String otp);


}
