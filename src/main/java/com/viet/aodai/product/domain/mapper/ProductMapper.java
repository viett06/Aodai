package com.viet.aodai.product.domain.mapper;

import com.viet.aodai.core.config.FileUpload;
import com.viet.aodai.product.domain.entity.Product;
import com.viet.aodai.product.domain.request.ProductRequest;
import com.viet.aodai.product.domain.response.ProductResponse;
import lombok.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
@Component
public class ProductMapper {

    private final FileUpload fileUpload;

    public Product toProduct(ProductRequest productRequest){
       return Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .brand(productRequest.getBrand())
                .image(convertImgUrl(productRequest.getImage()))
                .price(productRequest.getPrice())
                .build();
    }

    public ProductResponse toProductResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .image(product.getImage())
                .price(product.getPrice())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private String convertImgUrl(MultipartFile multipartFile){
        return fileUpload.uploadFile(multipartFile);
    }


}
