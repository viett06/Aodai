package com.viet.aodai.product.service.impl;

import com.viet.aodai.core.common.exception.AuthException;
import com.viet.aodai.core.common.exception.DuplicateResourceException;
import com.viet.aodai.product.domain.entity.Category;
import com.viet.aodai.product.domain.entity.Product;
import com.viet.aodai.product.domain.mapper.CategoryMapper;
import com.viet.aodai.product.domain.request.CategoryRequest;
import com.viet.aodai.product.domain.response.CategoryResponse;
import com.viet.aodai.product.repository.CategoryRepository;
import com.viet.aodai.product.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;


    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {

        if (categoryRepository.existsByNameIgnoreCase(categoryRequest.getName().trim())) {
            throw new DuplicateResourceException("Category with name '" + categoryRequest.getName() + "' already exists");
        }

        Category category = CategoryMapper.toCategory(categoryRequest);

        categoryRepository.save(category);

        log.info("Created new category: id={}, name={}", category.getCategoryId(), category.getCategoryName());
        return CategoryMapper.toCategoryResponse(category);
    }

    @Override
    public CategoryResponse getCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new AuthException("Category not found"));

        CategoryResponse categoryResponse = CategoryMapper.toCategoryResponse(category);

//        Set<Product> products = new HashSet<>();
//
//        for (Product product : category.getProducts()){
//            products.add(product);
//        }
//
//        categoryResponse.setProducts(products);

        categoryResponse.setProducts(category.getProducts());

        return categoryResponse;

    }
}
