package com.swiftcart.products.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.products.entity.OrdersEntity;
import com.swiftcart.products.service.OrdersService;

@RestController
@RequestMapping("/orders")
//@PreAuthorize("hasAuthority('ADMIN')")
public class OrdersController {

	@Autowired
	private OrdersService ordersService;

	@PostMapping()
	public ResponseEntity<Long> createOrders(@RequestBody OrdersEntity ordersEntity) {
		return new ResponseEntity<Long>(ordersService.createOrder(ordersEntity), null, HttpStatus.CREATED);
	}

	@GetMapping()
	public ResponseEntity<List<OrdersEntity>> getOrders() {
		List<OrdersEntity> orderss = ordersService.getOrders();
		return new ResponseEntity<List<OrdersEntity>>(orderss, null, HttpStatus.OK);
	}

	@GetMapping(path = "/{id}")
	public ResponseEntity<OrdersEntity> getOrders(@PathVariable Long id) {
		OrdersEntity entity = ordersService.getOrder(id);
		return new ResponseEntity<OrdersEntity>(entity, null, HttpStatus.OK);
	}

}
