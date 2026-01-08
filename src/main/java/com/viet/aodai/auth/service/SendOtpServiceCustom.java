package com.viet.aodai.auth.service;

import com.viet.aodai.auth.domain.enumration.MfaType;
import com.viet.aodai.core.common.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SendOtpServiceCustom {
//    private SendOtpService sendOtpService;
//
//    public SendOtpServiceCustom(SendOtpService sendOtpService) {
//        this.sendOtpService = sendOtpService;
//    }
//    public void sendOtp(String send, String otp){
//        sendOtpService.sendOtp(send, otp);
//    }
private final Map<String, SendOtpService> otpServices;

    public void sendOtp(MfaType type, String send, String otp) {
        SendOtpService service = otpServices.get(type.getBeanName());

        if (service == null) {
            throw new AuthException("Unsupported MFA type: " + type);
        }

        service.sendOtp(send, otp);
    }

}
