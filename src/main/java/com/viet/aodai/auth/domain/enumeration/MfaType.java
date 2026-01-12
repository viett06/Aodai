package com.viet.aodai.auth.domain.enumeration;

public enum MfaType {
    EMAIL("emailOtpService"),
    SMS("smsOtpService");

    private final String beanName;

    MfaType(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }
}
