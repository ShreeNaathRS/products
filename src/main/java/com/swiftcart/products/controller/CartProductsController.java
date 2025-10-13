package com.swiftcart.products.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.products.entity.CartProductsEntity;
import com.swiftcart.products.entity.LoginUserEntity;
import com.swiftcart.products.service.CartProductsService;
import com.swiftcart.products.util.TokenUtil;

@RestController
@RequestMapping("/cart-products")
public class CartProductsController {
	
	@Autowired
	TokenUtil tokenUtil;

	@Autowired
	private CartProductsService cartProductsService;

	@PostMapping()
	public ResponseEntity<Long> createCartProduct(@RequestBody CartProductsEntity cartProduct) {
		return new ResponseEntity<Long>(cartProductsService.createCartProduct(cartProduct), null, HttpStatus.CREATED);
	}

	@PutMapping()
	public ResponseEntity<CartProductsEntity> updateCartProduct(@RequestBody CartProductsEntity cartProduct) throws Exception{
		LoginUserEntity loginUser = tokenUtil.getLoggedInUserFromContext();
		return new ResponseEntity<CartProductsEntity>(cartProductsService.updateCartProduct(loginUser.getId(), cartProduct), null, HttpStatus.OK);
	}

	@DeleteMapping("/{cartProductId}")
	public ResponseEntity<String> deleteCartProduct(@PathVariable Long cartProductId) throws Exception{
		LoginUserEntity loginUser = tokenUtil.getLoggedInUserFromContext();
		cartProductsService.deleteCartProduct(loginUser.getId(), cartProductId);
		return new ResponseEntity<String>("Deleted", null, HttpStatus.OK);
	}

}
