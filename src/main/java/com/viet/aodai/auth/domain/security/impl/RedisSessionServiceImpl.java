package com.viet.aodai.auth.domain.security.impl;

import com.viet.aodai.auth.domain.security.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSessionServiceImpl implements SessionService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String SESSION_FREFIX = "auth:session:";
    private static final Duration SESSION_TTL = Duration.ofMinutes(10);
    @Override
    public String createSession(UUID userId) {
        String sessionToken = UUID.randomUUID().toString();
        String key = SESSION_FREFIX + userId;
        redisTemplate.opsForValue().set(key,sessionToken,SESSION_TTL);
        log.debug("Session created for user: {}", userId);
        return sessionToken;
    }

    @Override
    public Optional<String> getValidSession(UUID userId) {
        String key = SESSION_FREFIX +userId;
        String sessionToken = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(sessionToken);
    }

    @Override
    public void clearSession(UUID userId) {
        String key = SESSION_FREFIX + userId;
        redisTemplate.delete(key);
        log.debug("Session cleared for user: {}", userId);


    }

    @Override
    public boolean validateSession(String sessionToken) {
        return true;
    }
}
