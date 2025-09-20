package com.swiftcart.products.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swiftcart.products.entity.CartProductsEntity;
import com.swiftcart.products.repo.CartProductsRepo;
import com.swiftcart.products.service.CartProductsService;

@Service
public class CartProductsServiceImpl implements CartProductsService {

	@Autowired
	private CartProductsRepo cartProductsRepo;

	@Override
	public Long createCartProduct(CartProductsEntity cartProduct) {
		CartProductsEntity insertedCart = cartProductsRepo.save(cartProduct);
		return insertedCart.getId();
	}

	@Override
	public CartProductsEntity updateCartProduct(Long userId, CartProductsEntity cartProduct) throws Exception {
		CartProductsEntity dbEntity = cartProductsRepo.findById(cartProduct.getId())
				.orElseThrow(() -> new Exception("Entity not found"));
		if (!dbEntity.getCart().getUser().equals(userId)) {
			throw new Exception("You are not allowed to update other's cart");
		}
		cartProduct.setCart(dbEntity.getCart());
		cartProduct.setProduct(dbEntity.getProduct());
		return cartProductsRepo.save(cartProduct);
	}

	@Override
	public void deleteCartProduct(Long userId, Long cartProductId) throws Exception {
		CartProductsEntity cartProduct = cartProductsRepo.findById(cartProductId)
				.orElseThrow(() -> new Exception("Entity not found"));
		if (cartProduct.getCart().getUser().equals(userId)) {
			cartProductsRepo.deleteById(cartProductId);
		} else {
			throw new Exception("You are not allowed to delete other's cart");
		}
	}

}
