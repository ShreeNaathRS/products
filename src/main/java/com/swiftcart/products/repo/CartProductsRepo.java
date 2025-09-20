package com.swiftcart.products.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swiftcart.products.entity.CartProductsEntity;

public interface CartProductsRepo extends JpaRepository<CartProductsEntity, Long>{
}
