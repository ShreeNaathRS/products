package com.swiftcart.products.service.impl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swiftcart.products.entity.OrderProductsEntity;
import com.swiftcart.products.entity.OrdersEntity;
import com.swiftcart.products.entity.ProductEntity;
import com.swiftcart.products.repo.OrdersRepo;
import com.swiftcart.products.repo.ProductRepo;
import com.swiftcart.products.service.OrderProductsService;
import com.swiftcart.products.service.OrdersService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {

	@Autowired
	private OrdersRepo ordersRepo;
	
	@Autowired
	private OrderProductsService orderProductService;
	
	@Autowired
	private ProductRepo productRepo;

	@Override
	@Transactional
	public Long createOrder(OrdersEntity order) {
	    for (OrderProductsEntity item : order.getProducts()) {
	        item.setOrder(order); // link back to parent
	        ProductEntity managedProduct = productRepo.findById(item.getProduct().getId())
	            .orElseThrow(() -> new RuntimeException("Product not found"));
	        item.setProduct(managedProduct); // ensure it's attached
	    }

	    OrdersEntity savedOrder = ordersRepo.save(order); // cascade saves items
	    return savedOrder.getId();
	}
	
	@Override
	public List<OrdersEntity> getOrders() {
		return ordersRepo.findAll();
	}

	@Override
	public OrdersEntity getOrder(Long id) {
		Optional<OrdersEntity> user = ordersRepo.findById(id);
		return user.get();

	}
	
	@Override
	public List<OrdersEntity> getOrdersByUserid(Long id) {
		return ordersRepo.findByuser(id);
	}

}
