package com.viet.aodai.auth.service.impl;

import com.viet.aodai.auth.domain.enumration.AuthStep;
import com.viet.aodai.auth.domain.request.LoginRequest;
import com.viet.aodai.auth.domain.request.SelectMfaRequest;
import com.viet.aodai.auth.domain.request.VerifyMfaRequest;
import com.viet.aodai.auth.domain.response.AuthResponse;
import com.viet.aodai.auth.domain.security.JwtTokenProvider;
import com.viet.aodai.auth.service.AuthService;
import com.viet.aodai.auth.service.AuthStrategy;
import com.viet.aodai.auth.service.MfaService;
import com.viet.aodai.core.common.exception.*;
import com.viet.aodai.user.domain.entity.User;
import com.viet.aodai.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final List<AuthStrategy> authStrategies;
    private Map<AuthStep, AuthStrategy> strategyMap;

    @PostConstruct
    // tra cứu map thông qua key là step ở các class implement AuthStrategy
    public void init(){
        strategyMap = authStrategies.stream().collect(Collectors.toMap(
                AuthStrategy::getStep,
                Function.identity()
        ));
//        Map<AuthStep, AuthStrategy> map = new HashMap<>();
//
//        for (AuthStrategy strategy : authStrategies) {
//            AuthStep step = strategy.getStep();
//            map.put(step, strategy);
//        }
    }

    @Override
    public AuthResponse initialLogin(LoginRequest loginRequest) {
        log.info("Initial login attempt for username {}:", loginRequest.getUsername());

        AuthStrategy strategy = getStrategy(AuthStep.PASSWORD_VERIFY);
        return strategy.execute(loginRequest);
    }

    @Override
    public AuthResponse selectMfaMethod(SelectMfaRequest request) {
        log.info("MFA method selection for session: {}", request.getSessionToken());

        AuthStrategy strategy = getStrategy(AuthStep.MFA_REQUIRED);
        return strategy.execute(request);
    }

    @Override
    public AuthResponse verifyMfa(VerifyMfaRequest request) {
        log.info("MFA verification for session {}:", request.getSessionToken());

        AuthStrategy strategy = getStrategy(AuthStep.MFA_VERIFY);
        return strategy.execute(request);
    }

    // get strategy
    private AuthStrategy getStrategy(AuthStep authStep){
        AuthStrategy strategy = strategyMap.get(authStep);
        if (strategy ==null){
            throw new AuthException("No strategy found for step: " + authStep);

        }
        return strategy;
    }
}