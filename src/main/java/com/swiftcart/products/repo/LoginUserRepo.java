package com.swiftcart.products.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swiftcart.products.entity.LoginUserEntity;

public interface LoginUserRepo extends JpaRepository<LoginUserEntity, Long>{
	LoginUserEntity findByname(String name);
}
