package com.swiftcart.products.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swiftcart.products.entity.CartEntity;

public interface CartRepo extends JpaRepository<CartEntity, Long>{
	Optional<CartEntity> findByuser(Long userId);
}
