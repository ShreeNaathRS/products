package com.swiftcart.products.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swiftcart.products.entity.CategoryEntity;

public interface CategoryRepo extends JpaRepository<CategoryEntity, Long>{
}
