package com.viet.aodai.product.domain.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSearchRequestDTO {
    private String keyword;
    private String brand;
    private Long categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean inStock;

    @Builder.Default
    private String sortBy = "created_at";
    @Builder.Default
    private String sortDir = "DESC";

    @Builder.Default
    @Min(0)
    private int page = 0;

    @Builder.Default
    @Min(1) @Max(100)
    private int size =20;
}
