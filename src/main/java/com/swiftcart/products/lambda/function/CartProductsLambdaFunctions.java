package com.swiftcart.products.lambda.function;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.swiftcart.products.entity.CartProductsEntity;
import com.swiftcart.products.entity.LoginUserEntity;
import com.swiftcart.products.service.CartProductsService;
import com.swiftcart.products.util.TokenUtil;

@Component
public class CartProductsLambdaFunctions {
	private final TokenUtil tokenUtil;
	private final CartProductsService cartProductsService;

	public CartProductsLambdaFunctions(TokenUtil tokenUtil, CartProductsService cartProductsService) {
		this.tokenUtil = tokenUtil;
		this.cartProductsService = cartProductsService;
	}

	@Bean
	public Function<CartProductsEntity, ResponseEntity<Long>> createCartProduct() {
		return cartProduct -> new ResponseEntity<Long>(cartProductsService.createCartProduct(cartProduct), null,
				HttpStatus.CREATED);
	}

	@Bean
	public Function<CartProductsEntity, ResponseEntity<CartProductsEntity>> updateCartProduct() {
		return cartProduct -> {
			try {
				return new ResponseEntity<CartProductsEntity>(
						cartProductsService.updateCartProduct(tokenUtil.getLoggedInUserFromContext().getId(), cartProduct), null,
						HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		};
	}

	@Bean
	public Function<Long, ResponseEntity<String>> deleteCartProduct() {
		return cartProductId -> {
			try {
				LoginUserEntity loginUser = tokenUtil.getLoggedInUserFromContext();
				cartProductsService.deleteCartProduct(loginUser.getId(), cartProductId);
				return new ResponseEntity<String>("Deleted", null, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		};
	}
}
