package com.viet.aodai.product.repository;

import com.viet.aodai.product.domain.entity.Product;
import com.viet.aodai.product.repository.custom.ProductRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
}
