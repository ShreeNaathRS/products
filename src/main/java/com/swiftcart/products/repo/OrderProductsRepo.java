package com.swiftcart.products.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swiftcart.products.entity.OrderProductsEntity;

public interface OrderProductsRepo extends JpaRepository<OrderProductsEntity, Long>{
}
