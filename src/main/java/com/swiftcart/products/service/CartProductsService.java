package com.swiftcart.products.service;

import com.swiftcart.products.entity.CartProductsEntity;

public interface CartProductsService {

	Long createCartProduct(CartProductsEntity cartProduct);
	
	CartProductsEntity updateCartProduct(Long userId, CartProductsEntity cartProduct) throws Exception;

	public void deleteCartProduct(Long userId, Long cartProductId) throws Exception;

}
