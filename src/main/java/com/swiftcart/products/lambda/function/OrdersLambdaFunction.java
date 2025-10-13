package com.swiftcart.products.lambda.function;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.swiftcart.products.dto.PaginatedResponse;
import com.swiftcart.products.entity.OrdersEntity;
import com.swiftcart.products.service.OrdersService;

import lombok.Getter;
import lombok.Setter;

@Component
public class OrdersLambdaFunction {
	private final OrdersService ordersService;

	public OrdersLambdaFunction(OrdersService ordersService) {
		this.ordersService = ordersService;
	}

	@Bean
	public Function<OrdersEntity, ResponseEntity<Long>> createOrder() {
		return ordersEntity -> new ResponseEntity<Long>(ordersService.createOrder(ordersEntity), null, HttpStatus.CREATED);
	}
	
	@Bean
	public Supplier<ResponseEntity<List<OrdersEntity>>> getOrders() {
		return () -> new ResponseEntity<List<OrdersEntity>>(ordersService.getOrders(), null, HttpStatus.OK);
	}
	
	@Bean
	public Function<Long, ResponseEntity<OrdersEntity>> getOrderById() {
		return id -> new ResponseEntity<OrdersEntity>(ordersService.getOrder(id), null, HttpStatus.OK);
	}

	@Bean
	public Function<OrdersRequest, PaginatedResponse<OrdersEntity>> getOrdersByUserId(OrdersService ordersService) {
	    return request -> {
	        Sort.Direction sortDirection = Sort.Direction.fromString(request.getDirection());
	        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(sortDirection, request.getSort()));
	        Page<OrdersEntity> entityPage = ordersService.getOrdersByUserid(request.getUserId(), pageable);
	        return new PaginatedResponse<>(entityPage);
	    };
	}

}

@Getter
@Setter
class OrdersRequest {
    private Long userId;
    private int page;
    private int size;
    private String sort = "createdAt";
    private String direction = "DESC";
}