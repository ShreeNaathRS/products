package com.swiftcart.products.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.swiftcart.products.entity.OrdersEntity;

public interface OrdersRepo extends JpaRepository<OrdersEntity, Long>{
	Page<OrdersEntity> findByuser(Long user, Pageable pageable);
}
