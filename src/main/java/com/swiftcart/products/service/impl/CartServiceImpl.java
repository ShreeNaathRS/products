package com.swiftcart.products.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.swiftcart.products.entity.CartEntity;
import com.swiftcart.products.entity.CartProductsEntity;
import com.swiftcart.products.entity.ProductEntity;
import com.swiftcart.products.repo.CartRepo;
import com.swiftcart.products.repo.ProductRepo;
import com.swiftcart.products.service.CartService;
import com.swiftcart.products.util.TokenUtil;

import jakarta.transaction.Transactional;

@Service
public class CartServiceImpl implements CartService {

	private final CartRepo cartRepo;
    private final ProductRepo productRepo;
    private final TokenUtil tokenUtil;

    public CartServiceImpl(CartRepo cartRepo, ProductRepo productRepo, TokenUtil tokenUtil) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.tokenUtil = tokenUtil;
    }

	@Override
	public CartEntity createCart(CartEntity cart) throws Exception {
		List<CartProductsEntity> newProducts = new ArrayList<>();

		for (CartProductsEntity item : cart.getProducts()) {
			if (item.getProduct() == null || item.getProduct().getId() == null) {
				throw new Exception("Product must be provided with a valid ID");
			}

			CartProductsEntity newItem = new CartProductsEntity();
			newItem.setId(null); // ensure it's treated as new
			newItem.setCart(cart);

			ProductEntity managedProduct = productRepo.findById(item.getProduct().getId())
					.orElseThrow(() -> new Exception("Product not found"));
			newItem.setProduct(managedProduct);

			newItem.setQty(item.getQty());
			newItem.setPrice(item.getPrice());

			newProducts.add(newItem);
		}

		cart.setProducts(newProducts); // replace with clean, transient list

		return cartRepo.save(cart);
	}

	@Override
	@Transactional
	public CartEntity updateCart(CartEntity incomingCart) throws Exception {
		Long userId = tokenUtil.getLoggedInUserFromCustomContext().getId();
		CartEntity existingCart = cartRepo.findByuser(userId)
				.orElseThrow(() -> new Exception("Cart not found for user"));
		List<CartProductsEntity> existingProducts = existingCart.getProducts();
		List<Long> idsToRemove = existingCart.getProducts().stream()
				.filter(existing -> incomingCart.getProducts().stream()
						.noneMatch(incoming -> existing.getId().equals(incoming.getId())))
				.map(item -> item.getId()).collect(Collectors.toList());
		existingCart.getProducts().removeIf(existing->idsToRemove.contains(existing.getId()));
		for (CartProductsEntity incomingItem : incomingCart.getProducts()) {
			if (incomingItem.getProduct() == null || incomingItem.getProduct().getId() == null) {
				throw new Exception("Product must be provided with a valid ID");
			}
			ProductEntity managedProduct = productRepo.findById(incomingItem.getProduct().getId())
					.orElseThrow(() -> new Exception("Product not found"));
			CartProductsEntity match = existingProducts.stream()
					.filter(p -> p.getProduct().getId().equals(managedProduct.getId())).findFirst().orElse(null);
			if (match != null) {
				match.setQty(incomingItem.getQty());
				match.setPrice(incomingItem.getPrice());
			} else {
				CartProductsEntity newItem = new CartProductsEntity();
				newItem.setCart(existingCart);
				newItem.setProduct(managedProduct);
				newItem.setQty(incomingItem.getQty());
				newItem.setPrice(incomingItem.getPrice());
				existingProducts.add(newItem);
			}
		}
		return cartRepo.save(existingCart);
	}

	@Override
	public CartEntity getCart() throws Exception {
		Long userId = tokenUtil.getLoggedInUserFromCustomContext().getId();
		return cartRepo.findByuser(userId).orElseGet(() -> {return null;});
	}

	@Override
	public Long deleteCart() throws Exception {
		CartEntity cart = getCart();
		cartRepo.deleteById(cart.getId());
		return cart.getId();
	}

}
