package com.viet.aodai.product.domain.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.security.PrivateKey;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    @NotBlank(message = "Product name is required and cannot be empty")
    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotBlank(message = "Brand is required")
    @Size(min = 2, max = 100, message = "Brand must be between 2 and 100 characters")
    private String brand;

    private MultipartFile image;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 digits with 2 decimal places")
    private BigDecimal price;

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be a positive number")
    private Long categoryId;

    @NotNull(message = "Quantity ID is required")
    @Size(min = 2, max = 10000, message = "Brand must be between 2 and 100 characters")
    private Integer quantity;


}
