package com.swiftcart.products.lambda.function;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.swiftcart.products.entity.CategoryEntity;
import com.swiftcart.products.service.CategoryService;

@Component
public class CategoryLambdaFunctions {
	private final CategoryService categoryService;

	public CategoryLambdaFunctions(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	@Bean
	public Function<CategoryEntity, ResponseEntity<Long>> createCategory(){
		return category -> new ResponseEntity<Long>(categoryService.createCategory(category), null, HttpStatus.CREATED);
	}
	
	@Bean
	public Supplier<ResponseEntity<List<CategoryEntity>>> getCategories() {
		return () -> new ResponseEntity<List<CategoryEntity>>(categoryService.getCategories(), null, HttpStatus.OK);
	}

	@Bean
	public Function<Long, ResponseEntity<CategoryEntity>> getCategory(){
		return id -> new ResponseEntity<CategoryEntity>(categoryService.getCategory(id), null, HttpStatus.OK);
	}

	@Bean
	public Function<Long, ResponseEntity<Boolean>> deleteCategory() {
		return id -> new ResponseEntity<Boolean>(categoryService.deleteCategory(id), null, HttpStatus.OK);
	}

}
