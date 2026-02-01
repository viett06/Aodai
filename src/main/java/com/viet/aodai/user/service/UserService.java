package com.viet.aodai.user.service;

import com.viet.aodai.user.domain.request.RegisterRequest;
import com.viet.aodai.user.domain.response.UserResponse;
import com.viet.aodai.user.repository.UserRepository;

import java.util.UUID;

public interface UserService {
    UserResponse createUser(RegisterRequest request);
    UserResponse getUser(UUID userId);
}
