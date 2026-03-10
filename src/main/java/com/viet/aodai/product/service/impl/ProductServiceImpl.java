package com.viet.aodai.product.service.impl;

import com.viet.aodai.core.common.exception.AuthException;
import com.viet.aodai.product.domain.dto.CategoryDTO;
import com.viet.aodai.product.domain.dto.InventoryDTO;
import com.viet.aodai.product.domain.dto.ProductSearchResultDTO;
import com.viet.aodai.product.domain.entity.Category;
import com.viet.aodai.product.domain.entity.Inventory;
import com.viet.aodai.product.domain.entity.Product;
import com.viet.aodai.product.domain.mapper.ProductMapper;
import com.viet.aodai.product.domain.request.ProductRequest;
import com.viet.aodai.product.domain.request.ProductSearchRequestDTO;
import com.viet.aodai.product.domain.response.ProductResponse;
import com.viet.aodai.product.repository.CategoryRepository;
import com.viet.aodai.product.repository.InventoryRepository;
import com.viet.aodai.product.repository.ProductRepository;
import com.viet.aodai.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {

        Product product = productMapper.toProduct(productRequest);

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(()-> new AuthException("category not found"));

        product.setCategory(category);

        Inventory inventory = new Inventory();
        inventory.setQuantity(productRequest.getQuantity());
        inventoryRepository.save(inventory);

        product.setInventory(inventory);

        productRepository.save(product);

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getCategoryId());
        categoryDTO.setName(category.getCategoryName());

        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setInventoryId(inventory.getInventoryId());
        inventoryDTO.setQuantity(inventory.getQuantity());

        ProductResponse productResponse = productMapper.toProductResponse(product);
        productResponse.setCategoryDTO(categoryDTO);
        productResponse.setInventoryDTO(inventoryDTO);
        return productResponse;

    }

    @Override
    public Page<ProductSearchResultDTO> searchProducts(ProductSearchRequestDTO requestDTO, Pageable pageable) {
        validatePriceRange(requestDTO);
        return productRepository.searchProducts(requestDTO, pageable);
    }

    private void validatePriceRange(ProductSearchRequestDTO req) {
        if (req.getMinPrice() != null && req.getMaxPrice() != null
                && req.getMinPrice().compareTo(req.getMaxPrice()) > 0) {
            throw new IllegalArgumentException("minPrice phải nhỏ hơn hoặc bằng maxPrice");
        }
    }
}
