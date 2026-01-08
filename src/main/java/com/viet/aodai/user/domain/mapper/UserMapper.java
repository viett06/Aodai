package com.viet.aodai.user.domain.mapper;

import com.viet.aodai.user.domain.entity.User;
import com.viet.aodai.user.domain.request.RegisterRequest;
import com.viet.aodai.user.domain.response.UserResponse;

public class UserMapper {
    public static User toUser(RegisterRequest request){
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        return user;
    }
    public static UserResponse toUserResponse(User user){
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .role(user.getRole())
                .status(user.getStatus())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLogin(user.getLastLogin())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
