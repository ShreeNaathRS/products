package com.swiftcart.products.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.products.entity.SubCategoryEntity;
import com.swiftcart.products.service.SubCategoryService;

@RestController
@RequestMapping("/sub-category")
//@PreAuthorize("hasAuthority('ADMIN')")
public class SubCategoryController {

	@Autowired
	private SubCategoryService subCategoryService;

	@PostMapping()
	public ResponseEntity<Long> createProduct(@RequestBody SubCategoryEntity subCategoryEntity) {
		return new ResponseEntity<Long>(subCategoryService.createSubCategory(subCategoryEntity), null, HttpStatus.CREATED);
	}

	@GetMapping()
	public ResponseEntity<List<SubCategoryEntity>> getUsers() {
		List<SubCategoryEntity> categories = subCategoryService.getSubCategories();
		return new ResponseEntity<List<SubCategoryEntity>>(categories, null, HttpStatus.OK);
	}

	@GetMapping(path = "/{id}")
	public ResponseEntity<SubCategoryEntity> getUser(@PathVariable Long id) {
		SubCategoryEntity entity = subCategoryService.getSubCategory(id);
		return new ResponseEntity<SubCategoryEntity>(entity, null, HttpStatus.OK);
	}

	@DeleteMapping(path = "/{id}")
	public ResponseEntity<Boolean> deleteUser(@PathVariable Long id) {
		return new ResponseEntity<Boolean>(subCategoryService.deleteSubCategory(id), null, HttpStatus.OK);
	}

}
