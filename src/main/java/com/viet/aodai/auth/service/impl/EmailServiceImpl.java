package com.viet.aodai.auth.service.impl;

import com.viet.aodai.auth.service.SendOtpService;
import com.viet.aodai.core.common.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("emailOtpService")
@RequiredArgsConstructor
public class EmailServiceImpl implements SendOtpService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;


    @Override
    public void sendOtp(String email, String otp) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Your OTP code");
            message.setText("Your OTP code is: " + otp + "\nThis code will expire in 5 minutes.");
            mailSender.send(message);
        }
        catch (AuthException e){
            throw new AuthException("Failed to send OTP via Email");
        }
    }
}
