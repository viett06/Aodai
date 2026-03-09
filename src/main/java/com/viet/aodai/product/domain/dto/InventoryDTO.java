package com.viet.aodai.product.domain.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryDTO {

    private Long inventoryId;
    private Integer quantity;

}
