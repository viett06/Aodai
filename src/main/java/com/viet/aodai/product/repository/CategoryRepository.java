package com.viet.aodai.product.repository;

import com.viet.aodai.product.domain.entity.Category;
import com.viet.aodai.product.repository.custom.CategoryRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {

    @Query(
            value = """
                select count(*) > 0
                from category c
                where lower(c.category_name) = lower(:categoryName)
                """,
            nativeQuery = true
    )
    boolean existsByNameIgnoreCase(@Param("categoryName") String categoryName);

}
