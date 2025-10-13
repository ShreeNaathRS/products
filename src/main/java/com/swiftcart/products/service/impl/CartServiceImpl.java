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

    @Transactional
    @Override
    public CartEntity createCart(CartEntity cart) throws Exception {
        List<CartProductsEntity> newProducts = new ArrayList<>();

        for (CartProductsEntity item : cart.getProducts()) {
            Long productId = item.getProduct() != null ? item.getProduct().getId() : null;
            if (productId == null) {
                throw new IllegalArgumentException("Product must be provided with a valid ID");
            }

            ProductEntity managedProduct = productRepo.findById(productId)
                    .orElseThrow(() -> new Exception("Product not found"));

            CartProductsEntity newItem = new CartProductsEntity();
            newItem.setCart(cart);
            newItem.setProduct(managedProduct);
            newItem.setQty(item.getQty());
            newItem.setPrice(item.getPrice());

            newProducts.add(newItem);
        }

        cart.setProducts(newProducts);
        return cartRepo.save(cart);
    }

    @Override
    @Transactional
    public CartEntity updateCart(CartEntity incomingCart) throws Exception {
    	Long userId = tokenUtil.getLoggedInUserFromContext().getId();
        CartEntity existingCart = cartRepo.findByuser(userId)
                .orElseThrow(() -> new Exception("Cart not found for user"));

        List<CartProductsEntity> existingProducts = existingCart.getProducts();
        List<Long> incomingIds = incomingCart.getProducts().stream()
                .map(CartProductsEntity::getId)
                .collect(Collectors.toList());

        existingProducts.removeIf(existing -> !incomingIds.contains(existing.getId()));

        for (CartProductsEntity incomingItem : incomingCart.getProducts()) {
            Long productId = incomingItem.getProduct() != null ? incomingItem.getProduct().getId() : null;
            if (productId == null) {
                throw new IllegalArgumentException("Product must be provided with a valid ID");
            }

            ProductEntity managedProduct = productRepo.findById(productId)
                    .orElseThrow(() -> new Exception("Product not found"));

            CartProductsEntity match = existingProducts.stream()
                    .filter(p -> p.getProduct().getId().equals(productId))
                    .findFirst()
                    .orElse(null);

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
        Long userId = tokenUtil.getLoggedInUserFromContext().getId();
        return cartRepo.findByuser(userId).orElse(null);
    }

    @Override
    public Long deleteCart() throws Exception {
        CartEntity cart = getCart();
        if (cart == null) {
            throw new Exception("No cart found to delete");
        }
        cartRepo.deleteById(cart.getId());
        return cart.getId();
    }
}
