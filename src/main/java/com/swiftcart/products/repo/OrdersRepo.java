package com.swiftcart.products.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swiftcart.products.entity.OrdersEntity;

public interface OrdersRepo extends JpaRepository<OrdersEntity, Long>{
}
