package com.swiftcart.products.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swiftcart.products.entity.SubCategoryEntity;
import com.swiftcart.products.repo.SubCategoryRepo;
import com.swiftcart.products.service.SubCategoryService;

@Service
public class SubCategoryServiceImpl implements SubCategoryService {

	@Autowired
	private SubCategoryRepo subCategoryRepo;

	@Override
	public Long createSubCategory(SubCategoryEntity SubCategory) {
		SubCategoryEntity subCategoryEntity = subCategoryRepo.save(SubCategory);
		return subCategoryEntity.getId();
	}

	@Override
	public SubCategoryEntity getSubCategory(Long id) {
		Optional<SubCategoryEntity> subCategory = subCategoryRepo.findById(id);
		return subCategory.get();

	}

	@Override
	public List<SubCategoryEntity> getSubCategories() {
		return subCategoryRepo.findAll();
	}

	@Override
	public boolean deleteSubCategory(Long id) {
		SubCategoryEntity category = getSubCategory(id);
		if (category != null) {
			subCategoryRepo.deleteById(id);
			return true;
		}
		return false;
	}

}
