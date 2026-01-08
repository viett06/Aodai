package com.viet.aodai.auth.domain.security;

import java.util.Optional;
import java.util.UUID;

public interface SessionService {
    String createSession(UUID userId);
    Optional<String> getValidSession(UUID userId);
    void clearSession(UUID userId);
    boolean validateSession(String sessionToken);
}
