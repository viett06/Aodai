package com.viet.aodai.product.domain.response;

import com.viet.aodai.product.domain.entity.Product;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {
    private Long categoryId;
    private String categoryName;
    private String description;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Set<Product> products;

}
