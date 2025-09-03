package com.swiftcart.products.service;

import java.util.List;

import com.swiftcart.products.entity.CategoryEntity;

public interface CategoryService {
	
	Long createCategory(CategoryEntity Category);

	CategoryEntity getCategory(Long id);
	
	List<CategoryEntity> getCategories();

	boolean deleteCategory(Long id);
	
}
