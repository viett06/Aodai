package com.viet.aodai.user.controller;


import com.viet.aodai.user.domain.request.RegisterRequest;
import com.viet.aodai.user.domain.response.UserResponse;
import com.viet.aodai.user.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping("/user")
    public ResponseEntity<UserResponse> createUser(@RequestBody RegisterRequest request){
        UserResponse userResponse = userService.createUser(request);
        return ResponseEntity.ok(userResponse);
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("userId") UUID userId){
        UserResponse userResponse = userService.getUser(userId);
        return ResponseEntity.ok(userResponse);
    }

}
