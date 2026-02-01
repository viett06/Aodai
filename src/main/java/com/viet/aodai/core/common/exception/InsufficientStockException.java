package com.viet.aodai.core.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsufficientStockException extends RuntimeException {
    private Long id;
    private Integer inventoryQuantity;
    private Integer itemQuantity;
    public InsufficientStockException(String message, Long id, Integer inventoryQuantity, Integer itemQuantity) {
        super(message);
        this.id = id;
        this.inventoryQuantity = inventoryQuantity;
        this.itemQuantity = itemQuantity;

    }
}
