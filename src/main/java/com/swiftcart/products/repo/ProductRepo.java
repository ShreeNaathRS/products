package com.swiftcart.products.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swiftcart.products.entity.ProductEntity;

public interface ProductRepo extends JpaRepository<ProductEntity, Long>{
}
