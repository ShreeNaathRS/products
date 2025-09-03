package com.swiftcart.products.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swiftcart.products.entity.SubCategoryEntity;

public interface SubCategoryRepo extends JpaRepository<SubCategoryEntity, Long>{
}
