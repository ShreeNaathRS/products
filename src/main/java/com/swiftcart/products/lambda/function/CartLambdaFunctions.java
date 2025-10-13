package com.swiftcart.products.lambda.function;

import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import com.swiftcart.products.dto.ErrorResponse;
import com.swiftcart.products.entity.CartEntity;
import com.swiftcart.products.service.CartService;
import com.swiftcart.products.util.RequestContext;
import com.swiftcart.products.util.ResponseEntityUtil;
import com.swiftcart.products.util.TokenUtil;

@Component
public class CartLambdaFunctions {

	private final CartService cartService;
	
	private final TokenUtil tokenUtil;
	
	private final ResponseEntityUtil responseEntityUtil;

	public CartLambdaFunctions(CartService cartService, TokenUtil tokenUtil, ResponseEntityUtil responseEntityUtil) {
		this.cartService = cartService;
		this.tokenUtil = tokenUtil;
		this.responseEntityUtil = responseEntityUtil;
	}

	@Bean
	public Function<CartEntity, ResponseEntity<?>> createCart() {
		return cart -> {
			try {
				return new ResponseEntity<CartEntity>(cartService.createCart(cart), null, HttpStatus.CREATED);
			} catch (Exception e) {
				e.printStackTrace();
				return responseEntityUtil.getErrorResponseEntity(new ErrorResponse(CartEntity.class, RequestMethod.POST, "createCart"));
			}
		};
	}

	@Bean
	public Function<Message<CartEntity>, ResponseEntity<?>> updateCart() {
		return tokenUtil.authorized(cart -> {
			try {
				return new ResponseEntity<CartEntity>(cartService.updateCart(cart), null, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				return responseEntityUtil.getErrorResponseEntity(new ErrorResponse(CartEntity.class, RequestMethod.PUT, "updateCart"));
			} finally {
				RequestContext.clear();
			}
		}, "write:cart");
	}

	@Bean
	public Supplier<ResponseEntity<?>> getCart() {
		return () -> {
			try {
				return new ResponseEntity<CartEntity>(cartService.getCart(), null, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				return responseEntityUtil.getErrorResponseEntity(new ErrorResponse(CartEntity.class, RequestMethod.GET, "getCart"));
			}
		};
	}

	@Bean
	public Supplier<ResponseEntity<Long>> deleteCart() {
		return () -> {
			try {
				return new ResponseEntity<Long>(cartService.deleteCart(), null, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<Long>(0L, null, HttpStatus.BAD_REQUEST);
			}
		};
	}
}
