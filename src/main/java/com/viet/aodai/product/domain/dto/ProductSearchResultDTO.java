package com.viet.aodai.product.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class ProductSearchResultDTO {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private String image;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long categoryId;
    private String categoryName;
    private Integer stockQuantity;
}
