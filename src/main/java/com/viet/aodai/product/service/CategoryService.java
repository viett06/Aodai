package com.viet.aodai.product.service;

import com.viet.aodai.product.domain.entity.Category;
import com.viet.aodai.product.domain.request.CategoryRequest;
import com.viet.aodai.product.domain.response.CategoryResponse;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest categoryRequest);

    CategoryResponse getCategory(Long categoryId);
}
