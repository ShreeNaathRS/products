package com.swiftcart.products.lambda.function;

import java.util.function.Function;

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
	public Function<Message<CartEntity>, ?> createCart() {
	    return tokenUtil.authorized(cart -> {
	        try {
	            return ResponseEntity.status(HttpStatus.CREATED).body(cartService.createCart(cart));
	        } catch (Exception e) {
	            e.printStackTrace();
	            return responseEntityUtil.getErrorResponseEntity(
	                new ErrorResponse(CartEntity.class, RequestMethod.POST, "createCart")
	            );
	        }
	    }, "ADMIN,NORMAL");
	}

	@Bean
	public Function<Message<CartEntity>, ?> updateCart() {
	    return tokenUtil.authorized(cart -> {
	        try {
	            return ResponseEntity.ok(cartService.updateCart(cart));
	        } catch (Exception e) {
	            e.printStackTrace();
	            return responseEntityUtil.getErrorResponseEntity(
	                new ErrorResponse(CartEntity.class, RequestMethod.PUT, "updateCart")
	            );
	        } finally {
	            RequestContext.clear();
	        }
	    }, "ADMIN,NORMAL");
	}


	@Bean
	public Function<Message<Object>, Object> getCart() {
	    return message -> {
	        try {
	            return tokenUtil.authorized(
	                payload -> {
	                    try {
	                        return ResponseEntity.ok(cartService.getCart());
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                        return responseEntityUtil.getErrorResponseEntity(
	                            new ErrorResponse(CartEntity.class, RequestMethod.GET, "getCart")
	                        );
	                    }
	                },
	                "ADMIN,NORMAL"
	            ).apply(message);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "Unexpected error: " + e.getMessage();
	        }
	    };
	}


	@Bean
	public Function<Message<Object>, Object> deleteCart() {
	    return message -> {
	        try {
	            return tokenUtil.authorized(msg -> {
	                try {
	                    Long deletedId = cartService.deleteCart();
	                    return new ResponseEntity<>(deletedId, HttpStatus.OK);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                    return new ResponseEntity<>(0L, HttpStatus.BAD_REQUEST);
	                }
	            }, "ADMIN,NORMAL").apply(message);

	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
	        }
	    };
	}

}
