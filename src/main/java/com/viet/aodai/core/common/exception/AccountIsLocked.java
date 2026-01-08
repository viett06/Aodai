package com.viet.aodai.core.common.exception;

public class AccountIsLocked extends RuntimeException {
    public AccountIsLocked(String message) {
        super(message);
    }
}
