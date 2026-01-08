package com.viet.aodai.auth.domain.enumration;

public enum AuthStep {
    PASSWORD_VERIFY,
    MFA_REQUIRED,
    MFA_VERIFY,
    COMPLETE,
    FAILED
}
