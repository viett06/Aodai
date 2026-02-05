package com.viet.aodai.product.repository;

import com.viet.aodai.product.domain.entity.Product;
import com.viet.aodai.product.repository.impl.ProductRepositoryImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface productRepository extends JpaRepository<Product, Long>, ProductRepositoryImpl {
}
