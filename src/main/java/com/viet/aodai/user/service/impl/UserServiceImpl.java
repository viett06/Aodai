package com.viet.aodai.user.service.impl;


import com.viet.aodai.user.domain.dto.UserRole;
import com.viet.aodai.user.domain.dto.UserStatus;
import com.viet.aodai.user.domain.entity.User;
import com.viet.aodai.core.common.exception.UserNotFoundException;
import com.viet.aodai.user.domain.mapper.UserMapper;
import com.viet.aodai.user.domain.request.RegisterRequest;
import com.viet.aodai.user.domain.response.UserResponse;
import com.viet.aodai.user.repository.UserRepository;
import com.viet.aodai.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(RegisterRequest request){
        User user = UserMapper.toUser(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(UserRole.CUSTOMER);
        userRepository.save(user);
        return UserMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUser(UUID userId){
        User user = userRepository.findUserById(userId).orElseThrow(()-> new UserNotFoundException("user not found"));
        return UserMapper.toUserResponse(user);
    }

}
