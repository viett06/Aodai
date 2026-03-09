package com.viet.aodai.product.repository.impl;

import com.viet.aodai.product.domain.dto.ProductSearchResultDTO;
import com.viet.aodai.product.domain.request.ProductSearchRequestDTO;
import org.springframework.data.domain.Page;

public interface ProductRepositoryImpl{

    Page<ProductSearchResultDTO> searchProducts(ProductSearchRequestDTO request);

}
