package com.swiftcart.products.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swiftcart.products.entity.OrdersEntity;

public interface OrdersRepo extends JpaRepository<OrdersEntity, Long>{
	List<OrdersEntity> findByuser(Long user);
}
