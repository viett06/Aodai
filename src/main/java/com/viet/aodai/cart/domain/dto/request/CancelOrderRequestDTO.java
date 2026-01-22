package com.viet.aodai.cart.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelOrderRequestDTO {
    @NotNull
    private Long orderId;

    @NotBlank
    private String reason;

    private String notes;

    @NotBlank
    private String cancelledBy; // USER, ADMIN
}
