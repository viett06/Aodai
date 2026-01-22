package com.viet.aodai.cart.domain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequestDTO {
    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    private Integer quantity;

    // For reorder - to confirm current price
    private BigDecimal expectedPrice;
}
