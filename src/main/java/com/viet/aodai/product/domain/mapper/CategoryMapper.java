package com.viet.aodai.product.domain.mapper;

import com.viet.aodai.product.domain.entity.Category;
import com.viet.aodai.product.domain.request.CategoryRequest;
import com.viet.aodai.product.domain.response.CategoryResponse;
import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class CategoryMapper {

    public static Category toCategory(CategoryRequest categoryRequest){
        Category category = new Category();
        category.setCategoryName(category.getCategoryName());
        category.setDescription(categoryRequest.getDescription());
        return category;
    }

    public static CategoryResponse toCategoryResponse(Category category){
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .createAt(category.getCreateAt())
                .updateAt(category.getUpdateAt())
                .build();
    }
}
