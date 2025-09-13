package com.swiftcart.products.service;

import java.util.List;

import com.swiftcart.products.entity.OrdersEntity;

public interface OrdersService {

	Long createOrder(OrdersEntity product);
	
	List<OrdersEntity> getOrders();

	OrdersEntity getOrder(Long id);

}
