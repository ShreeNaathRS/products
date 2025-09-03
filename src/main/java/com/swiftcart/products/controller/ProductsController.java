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

import com.swiftcart.products.entity.ProductEntity;
import com.swiftcart.products.service.ProductService;

@RestController
@RequestMapping("/products")
//@PreAuthorize("hasAuthority('ADMIN')")
public class ProductsController {

	@Autowired
	private ProductService productService;

	@PostMapping()
	public ResponseEntity<Long> createProduct(@RequestBody ProductEntity productEntity) {
		return new ResponseEntity<Long>(productService.createProduct(productEntity), null, HttpStatus.CREATED);
	}

	@GetMapping()
	public ResponseEntity<List<ProductEntity>> getProducts() {
		List<ProductEntity> products = productService.getProducts();
		return new ResponseEntity<List<ProductEntity>>(products, null, HttpStatus.OK);
	}

	@GetMapping(path = "/{id}")
	public ResponseEntity<ProductEntity> getProduct(@PathVariable Long id) {
		ProductEntity entity = productService.getProduct(id);
		return new ResponseEntity<ProductEntity>(entity, null, HttpStatus.OK);
	}

	@DeleteMapping(path = "/{id}")
	public ResponseEntity<Boolean> deleteProduct(@PathVariable Long id) {
		return new ResponseEntity<Boolean>(productService.deleteProduct(id), null, HttpStatus.OK);
	}

}
