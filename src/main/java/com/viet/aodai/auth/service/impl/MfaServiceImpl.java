package com.viet.aodai.auth.service.impl;

import com.viet.aodai.auth.domain.entity.MfaOtp;
import com.viet.aodai.auth.domain.enumeration.MfaType;
import com.viet.aodai.auth.repository.MfaOtpRepository;
import com.viet.aodai.auth.service.MfaService;
import com.viet.aodai.auth.service.SendOtpServiceCustom;
import com.viet.aodai.core.common.exception.AuthException;
import com.viet.aodai.core.config.PasswordEncoderConfig;
import com.viet.aodai.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service
public class MfaServiceImpl implements MfaService {

    private final PasswordEncoderConfig passwordEncoderConfig;
    private final MfaOtpRepository mfaOtpRepository;
    private final SendOtpServiceCustom sendOtpServiceCustom;

    @Override
    public void generateAndSendOtp(User user, MfaType mfaType) {

        String otp = generateOtp();
        saveOtpToDatabase(user,otp,mfaType);
        sendOtpToUser(user,otp,mfaType);

    }

    @Override
    public boolean verifyOtp(UUID userId, String otp) {
        MfaOtp mfaOtp = mfaOtpRepository.findValidOtp(userId)
                .orElseThrow(()-> new AuthException("Invalid or Expired OTP"));
        // verify OTP
        if (!passwordEncoderConfig.passwordEncoder().matches(otp,mfaOtp.getOtpHash())){
            mfaOtp.incrementAttempt();
            mfaOtpRepository.save(mfaOtp);
            if (mfaOtp.getAttemptCount() >=5){
                mfaOtp.setUsed(true);
                mfaOtpRepository.save(mfaOtp);
                throw new AuthException("Too many attempt. Otp invalidated");
            }
            return false;
        }
        // Mark OTP as used
        mfaOtp.setUsed(true);
        mfaOtp.setVerifiedAt(LocalDateTime.now());
        mfaOtpRepository.save(mfaOtp);

        // Cleanup old OTPs
        cleanupOldOtps(userId);

        return true;
    }
    private String generateOtp() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 999999));
    }

    private void saveOtpToDatabase(User user, String otp, MfaType mfaType){
        MfaOtp mfaOtp = MfaOtp.builder()
                .user(user)
                .otpHash(passwordEncoderConfig.passwordEncoder().encode(otp))
                .type(mfaType)
                .expiredAt(LocalDateTime.now())
                .used(false)
                .build();

        mfaOtpRepository.save(mfaOtp);
    }

    private void sendOtpToUser(User user, String otp, MfaType mfaType){
        String target = switch (mfaType){
            case EMAIL -> user.getEmail();

            case SMS ->  user.getPhoneNumber();
        };
        sendOtpServiceCustom.sendOtp(mfaType,target,otp);
    }

    private void cleanupOldOtps(UUID userId){
        mfaOtpRepository.deleteExpiredOtps(userId);
    }
}
