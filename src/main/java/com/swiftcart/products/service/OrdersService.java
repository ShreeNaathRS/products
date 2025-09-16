package com.swiftcart.products.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.swiftcart.products.entity.OrdersEntity;

public interface OrdersService {

	Long createOrder(OrdersEntity product);
	
	List<OrdersEntity> getOrders();

	OrdersEntity getOrder(Long id);
	
	Page<OrdersEntity> getOrdersByUserid(Long id, Pageable pageable);

}
