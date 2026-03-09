package com.viet.aodai.product.controller;

import com.viet.aodai.product.domain.dto.ProductSearchResultDTO;
import com.viet.aodai.product.domain.request.ProductSearchRequestDTO;
import com.viet.aodai.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/search")
    public ResponseEntity<Page<ProductSearchResultDTO>> search(
            @Valid @ModelAttribute ProductSearchRequestDTO request) {
        return ResponseEntity.ok(productService.searchProducts(request));
    }
}
