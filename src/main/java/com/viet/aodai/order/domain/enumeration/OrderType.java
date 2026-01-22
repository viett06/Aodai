package com.viet.aodai.order.domain.enumeration;

public enum OrderType {
    FROM_CART("Từ giỏ hàng"),
    BUY_NOW("Mua ngay"),
    REORDER("Mua lại"),
    QUICK_CHECKOUT("Thanh toán nhanh");

    private final String description;

    OrderType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
