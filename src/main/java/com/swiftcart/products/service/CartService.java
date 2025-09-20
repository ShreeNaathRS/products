package com.swiftcart.products.service;

import com.swiftcart.products.entity.CartEntity;

public interface CartService {

	CartEntity createCart(CartEntity cart) throws Exception;
	
	CartEntity updateCart(CartEntity cart) throws Exception;
	
	CartEntity getCart() throws Exception;

	Long deleteCart() throws Exception;

}
