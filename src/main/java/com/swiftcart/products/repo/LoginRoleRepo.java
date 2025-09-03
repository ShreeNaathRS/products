package com.swiftcart.products.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swiftcart.products.entity.LoginRoleEntity;

public interface LoginRoleRepo extends JpaRepository<LoginRoleEntity, Long> {

}
