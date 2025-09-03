package com.swiftcart.products.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swiftcart.products.entity.ProductEntity;
import com.swiftcart.products.repo.ProductRepo;
import com.swiftcart.products.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepo productRepo;

	@Override
	public Long createProduct(ProductEntity user) {
		ProductEntity userEntity = productRepo.save(user);
		return userEntity.getId();
	}
	
	@Override
	public List<ProductEntity> getProducts() {
		return productRepo.findAll();
	}

	@Override
	public ProductEntity getProduct(Long id) {
		Optional<ProductEntity> user = productRepo.findById(id);
		return user.get();

	}

	@Override
	public boolean deleteProduct(Long id) {
		ProductEntity user = getProduct(id);
		if (user != null) {
			productRepo.deleteById(id);
			return true;
		}
		return false;
	}

}
