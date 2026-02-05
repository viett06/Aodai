package com.viet.aodai.order.domain.enumeration;

public enum OrderType {
    FROM_CART("fromCash"),
    BUY_NOW("buyNow"),
    REORDER("reOrder"),
    QUICK_CHECKOUT("quickOrder");

    private final String description;

    OrderType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
