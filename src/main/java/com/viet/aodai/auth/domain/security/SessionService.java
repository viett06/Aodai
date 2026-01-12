package com.viet.aodai.auth.domain.security;

import com.viet.aodai.auth.domain.dto.PendingAuthSession;
import com.viet.aodai.auth.domain.enumeration.AuthStep;
import com.viet.aodai.auth.domain.enumeration.MfaType;

import java.util.Optional;
import java.util.UUID;

public interface SessionService {
   String generateSessionToken(UUID userId, String username, String deviceFingerprint);
   Optional<PendingAuthSession> getSession(String sessionToken);
   void updateSessionToken(String sessionToken, AuthStep newStep, MfaType mfaType);
   void markOtpSent(String sessionToken);
   void invalidateSession(String sessionToken);
   void invalidateAllUserSessions(UUID userId);
   void cleanupExpiredSession();
}
