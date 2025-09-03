package com.swiftcart.products.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swiftcart.products.entity.CategoryEntity;
import com.swiftcart.products.repo.CategoryRepo;
import com.swiftcart.products.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepo categoryRepo;

	@Override
	public Long createCategory(CategoryEntity Category) {
		CategoryEntity CategoryEntity = categoryRepo.save(Category);
		return CategoryEntity.getId();
	}

	@Override
	public CategoryEntity getCategory(Long id) {
		Optional<CategoryEntity> Category = categoryRepo.findById(id);
		return Category.get();
	}
	
	@Override
	public List<CategoryEntity> getCategories() {
		return categoryRepo.findAll();
	}

	@Override
	public boolean deleteCategory(Long id) {
		CategoryEntity category = getCategory(id);
		if (category != null) {
			categoryRepo.deleteById(id);
			return true;
		}
		return false;
	}

}
