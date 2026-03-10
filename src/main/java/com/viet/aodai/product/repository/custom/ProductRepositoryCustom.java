package com.viet.aodai.product.repository.custom;

import com.viet.aodai.product.domain.dto.ProductSearchResultDTO;
import com.viet.aodai.product.domain.request.ProductSearchRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    Page<ProductSearchResultDTO> searchProducts(ProductSearchRequestDTO request, Pageable pageable);

}
