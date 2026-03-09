package com.viet.aodai.product.domain.response;

import com.viet.aodai.product.domain.dto.CategoryDTO;
import com.viet.aodai.product.domain.dto.InventoryDTO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {

    private Long id;

    private String name;

    private String description;

    private String brand;

    private String image;

    private BigDecimal price;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private CategoryDTO categoryDTO;

    private InventoryDTO inventoryDTO;

}
