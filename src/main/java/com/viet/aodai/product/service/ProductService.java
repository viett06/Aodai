package com.viet.aodai.product.service;

import com.viet.aodai.product.domain.dto.ProductSearchResultDTO;
import com.viet.aodai.product.domain.request.ProductRequest;
import com.viet.aodai.product.domain.request.ProductSearchRequestDTO;
import com.viet.aodai.product.domain.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest);

    Page<ProductSearchResultDTO> searchProducts(ProductSearchRequestDTO requestDTO, Pageable pageable);
}
