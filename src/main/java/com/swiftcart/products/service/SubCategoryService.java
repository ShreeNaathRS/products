package com.swiftcart.products.service;

import java.util.List;

import com.swiftcart.products.entity.SubCategoryEntity;

public interface SubCategoryService {
	
	Long createSubCategory(SubCategoryEntity SubCategory);

	SubCategoryEntity getSubCategory(Long id);
	
	List<SubCategoryEntity> getSubCategories();

	boolean deleteSubCategory(Long id);

}
