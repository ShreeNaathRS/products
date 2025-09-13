package com.swiftcart.products.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swiftcart.products.entity.OrderProductsEntity;
import com.swiftcart.products.repo.OrderProductsRepo;
import com.swiftcart.products.service.OrderProductsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderProductsServiceImpl implements OrderProductsService {

	@Autowired
	private OrderProductsRepo orderProdRepo;

	@Override
	public OrderProductsEntity createOrderProducts(OrderProductsEntity orderProduct) {
		OrderProductsEntity entity = orderProdRepo.save(orderProduct);
		return entity;
	}
	
}
