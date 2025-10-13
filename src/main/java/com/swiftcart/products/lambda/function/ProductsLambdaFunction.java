package com.swiftcart.products.lambda.function;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.swiftcart.products.entity.ProductEntity;
import com.swiftcart.products.service.ProductService;

@Component
public class ProductsLambdaFunction {

	private final ProductService productService;

	public ProductsLambdaFunction(ProductService productService) {
		this.productService = productService;
	}

	@Bean
	public Function<ProductEntity, ResponseEntity<Long>> createProduct() {
		return productEntity -> new ResponseEntity<Long>(productService.createProduct(productEntity), null, HttpStatus.CREATED);
	}

	@Bean
	public Supplier<ResponseEntity<List<ProductEntity>>> getProducts() {
		return () -> new ResponseEntity<List<ProductEntity>>(productService.getProducts(), null, HttpStatus.OK);
	}

	@Bean
	public Function<Long, ResponseEntity<ProductEntity>> getProduct() {
		return id -> new ResponseEntity<ProductEntity>(productService.getProduct(id), null, HttpStatus.OK);
	}

	@Bean
	public Function<Long, ResponseEntity<Boolean>> deleteProduct() {
		return id -> new ResponseEntity<Boolean>(productService.deleteProduct(id), null, HttpStatus.OK);
	}
}
