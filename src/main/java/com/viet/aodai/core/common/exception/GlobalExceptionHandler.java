package com.viet.aodai.core.common.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationException(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleUserNotFoundException(UsernameNotFoundException ex){
        log.warn("Patient Not Found {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Patient Not Found");
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(StatusUserException.class)
    public ResponseEntity<Map<String,String>> handleStatusUserException(StatusUserException ex){
        log.warn("Account error status {}",ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message","Account error status");
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(PassWordErrorException.class)
    public ResponseEntity<Map<String,String>> handlePassWordErrorException(StatusUserException ex){
        log.warn("PassWord not correct {}",ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message","PassWord not correct");
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(AccountIsLocked.class)
    public ResponseEntity<Map<String,String>> handleAccountIsLocked(StatusUserException ex){
        log.warn("Account is locked {}",ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message","Account is locked");
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String,String>> handleAuthException(StatusUserException ex){
        log.warn("Auth error {}",ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message","Auth error");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientStockException(InsufficientStockException ex){
        Map<String, Object> errors = new HashMap<>();
        errors.put("id", ex.getId());
        errors.put("inventoryQuantity", ex.getInventoryQuantity());
        errors.put("itemQuantity", ex.getItemQuantity());
        return ResponseEntity.ok().body(errors);
    }



}
