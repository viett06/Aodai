package com.viet.aodai.auth.service;

import org.springframework.stereotype.Repository;


public interface SendOtpService {
    void sendOtp(String send, String otp);

}
