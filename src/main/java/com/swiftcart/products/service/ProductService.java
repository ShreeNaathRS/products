package com.swiftcart.products.service;

import java.util.List;

import com.swiftcart.products.entity.ProductEntity;

public interface ProductService {

	Long createProduct(ProductEntity product);
	
	List<ProductEntity>getProducts();

	ProductEntity getProduct(Long id);

	boolean deleteProduct(Long id);

}
