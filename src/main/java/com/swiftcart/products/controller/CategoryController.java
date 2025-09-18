package com.swiftcart.products.controller;

import static com.swiftcart.products.constants.AuthorityConstants.ADMIN_AUTHORITY;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.products.entity.CategoryEntity;
import com.swiftcart.products.service.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@PreAuthorize(ADMIN_AUTHORITY)
	@PostMapping()
	public ResponseEntity<Long> createProduct(@RequestBody CategoryEntity categoryEntity) {
		return new ResponseEntity<Long>(categoryService.createCategory(categoryEntity), null, HttpStatus.CREATED);
	}

	@GetMapping()
	public ResponseEntity<List<CategoryEntity>> getCategories() {
		List<CategoryEntity> categories = categoryService.getCategories();
		return new ResponseEntity<List<CategoryEntity>>(categories, null, HttpStatus.OK);
	}

	@GetMapping(path = "/{id}")
	public ResponseEntity<CategoryEntity> getUser(@PathVariable Long id) {
		CategoryEntity entity = categoryService.getCategory(id);
		return new ResponseEntity<CategoryEntity>(entity, null, HttpStatus.OK);
	}

	@PreAuthorize(ADMIN_AUTHORITY)
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<Boolean> deleteUser(@PathVariable Long id) {
		return new ResponseEntity<Boolean>(categoryService.deleteCategory(id), null, HttpStatus.OK);
	}

}
