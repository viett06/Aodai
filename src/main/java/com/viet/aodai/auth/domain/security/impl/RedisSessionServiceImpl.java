package com.viet.aodai.auth.domain.security.impl;

import com.viet.aodai.auth.domain.dto.PendingAuthSession;
import com.viet.aodai.auth.domain.enumeration.AuthStep;
import com.viet.aodai.auth.domain.enumeration.MfaType;
import com.viet.aodai.auth.domain.security.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSessionServiceImpl implements SessionService {

    private final Map<String, PendingAuthSession> sessions = new ConcurrentHashMap<>();

    private final Map<String, Set<String>> userSessionsIndex = new ConcurrentHashMap<>();

    @Override
    public String generateSessionToken(UUID userId, String username) {
        String sessionToken = UUID.randomUUID().toString();

        PendingAuthSession session = PendingAuthSession.builder()
                .userId(userId)
                .username(username)
                .currentStep(AuthStep.PASSWORD_VERIFY)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(1))
                .otpSent(false)
                .build();
        sessions.put(sessionToken,session);

        // computeIfAbsent always return Set<String>
        userSessionsIndex.computeIfAbsent(userId.toString(), k -> ConcurrentHashMap.newKeySet()).add(sessionToken);

        return sessionToken;

    }

    //get session and validate
    @Override
    public Optional<PendingAuthSession> getSession(String sessionToken) {
        PendingAuthSession session = sessions.get(sessionToken);

        if (session == null){
            return Optional.empty();
        }

        if (session.getExpiresAt().isBefore(LocalDateTime.now())){
            invalidateSession(sessionToken);
            return Optional.empty();
        }
        return Optional.of(session);
    }

    @Override
    public void updateSessionToken(String sessionToken, AuthStep newStep, MfaType mfaType) {
        PendingAuthSession session = sessions.get(sessionToken);
        if (session != null){
            session.setCurrentStep(newStep);
            if (mfaType != null){
                session.setSelectedMfaType(mfaType);
            }
        }

    }

    // hide otp sended
    @Override
    public void markOtpSent(String sessionToken) {
        PendingAuthSession session = sessions.get(sessionToken);
        if (session != null) {
            session.setOtpSent(true);
        }

    }

    @Override
    public void invalidateSession(String sessionToken) {
        PendingAuthSession session = sessions.remove(sessionToken);
        if (session != null){
            Set<String> userSessions = userSessionsIndex.get(session.getUserId().toString());
            if (userSessions != null){
                userSessions.remove(sessionToken);
            }
        }
    }

    @Override
    public void invalidateAllUserSessions(UUID userId) {
        Set<String> userSessions = userSessionsIndex.remove(userId.toString());
        if (userSessions != null){
            userSessions.forEach(sessions::remove);
        }

    }

    // clean periodic
    @Override
    public void cleanupExpiredSession() {
        LocalDateTime now = LocalDateTime.now();
        sessions.entrySet().removeIf(entry -> entry.getValue().getExpiresAt().isBefore(now));
    }
}
