package com.swiftcart.products.lambda.function;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.swiftcart.products.entity.SubCategoryEntity;
import com.swiftcart.products.service.SubCategoryService;

@Component
public class SubCategoryLambdaFunction {

	private final SubCategoryService subCategoryService;
	public SubCategoryLambdaFunction(SubCategoryService subCategoryService) {
		this.subCategoryService=subCategoryService;
	}

	@Bean
	public Function<SubCategoryEntity, ResponseEntity<Long>> createSubCategory() {
		return subCategoryEntity -> new ResponseEntity<Long>(subCategoryService.createSubCategory(subCategoryEntity), null, HttpStatus.CREATED); 
	}

	@Bean
	public Supplier<ResponseEntity<List<SubCategoryEntity>>> getSubCategories() {
		return () -> new ResponseEntity<List<SubCategoryEntity>>(subCategoryService.getSubCategories(), null, HttpStatus.OK);
	}

	@Bean
	public Function<Long, ResponseEntity<SubCategoryEntity>> getSubCategoryById() {
		return id -> new ResponseEntity<SubCategoryEntity>(subCategoryService.getSubCategory(id), null, HttpStatus.OK);
	}

	@Bean
	public Function<Long, ResponseEntity<Boolean>> deleteSubCategoryById() {
		return id -> new ResponseEntity<Boolean>(subCategoryService.deleteSubCategory(id), null, HttpStatus.OK);
	}
}
