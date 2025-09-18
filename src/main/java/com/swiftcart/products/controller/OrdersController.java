package com.swiftcart.products.controller;

import static com.swiftcart.products.constants.AuthorityConstants.ADMIN_AUTHORITY;
import static com.swiftcart.products.constants.AuthorityConstants.ADMIN_OR_NORMAL_AUTHORITY;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.products.dto.PaginatedResponse;
import com.swiftcart.products.entity.OrdersEntity;
import com.swiftcart.products.service.OrdersService;

@RestController
@RequestMapping("/orders")
public class OrdersController {

	@Autowired
	private OrdersService ordersService;

	@PreAuthorize(ADMIN_OR_NORMAL_AUTHORITY)
	@PostMapping()
	public ResponseEntity<Long> createOrders(@RequestBody OrdersEntity ordersEntity) {
		return new ResponseEntity<Long>(ordersService.createOrder(ordersEntity), null, HttpStatus.CREATED);
	}

	@PreAuthorize(ADMIN_AUTHORITY)
	@GetMapping()
	public ResponseEntity<List<OrdersEntity>> getOrders() {
		List<OrdersEntity> orderss = ordersService.getOrders();
		return new ResponseEntity<List<OrdersEntity>>(orderss, null, HttpStatus.OK);
	}

	@PreAuthorize(ADMIN_AUTHORITY)
	@GetMapping(path = "/{id}")
	public ResponseEntity<OrdersEntity> getOrders(@PathVariable Long id) {
		OrdersEntity entity = ordersService.getOrder(id);
		return new ResponseEntity<OrdersEntity>(entity, null, HttpStatus.OK);
	}
	
	@PreAuthorize(ADMIN_OR_NORMAL_AUTHORITY)
	@GetMapping(path = "/byUser/{id}")
	public ResponseEntity<PaginatedResponse<OrdersEntity>> getOrdersByUserid(@PathVariable Long id, 
			@PageableDefault(sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable) {
		Page<OrdersEntity> entity = ordersService.getOrdersByUserid(id, pageable);
		PaginatedResponse<OrdersEntity> paginatedResponse = new PaginatedResponse<>(entity);
		return new ResponseEntity<PaginatedResponse<OrdersEntity>>(paginatedResponse, null, HttpStatus.OK);
	}

}
