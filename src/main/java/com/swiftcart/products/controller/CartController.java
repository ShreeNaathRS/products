package com.swiftcart.products.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.products.entity.CartEntity;
import com.swiftcart.products.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Autowired
	private CartService cartService;

	@PostMapping()
	public ResponseEntity<CartEntity> createCart(@RequestBody CartEntity cartEntity) throws Exception {
		return new ResponseEntity<CartEntity>(cartService.createCart(cartEntity), null, HttpStatus.CREATED);
	}
	
	@PutMapping()
	public ResponseEntity<CartEntity> updateCart(@RequestBody CartEntity cartEntity) throws Exception {
		return new ResponseEntity<CartEntity>(cartService.updateCart(cartEntity), null, HttpStatus.OK);
	}

	@GetMapping()
	public ResponseEntity<CartEntity> getCart() throws Exception {
		return new ResponseEntity<CartEntity>(cartService.getCart(), null, HttpStatus.OK);
	}

	@DeleteMapping()
	public ResponseEntity<Long> deleteCart() throws Exception {
		return new ResponseEntity<Long>(cartService.deleteCart(), null, HttpStatus.OK);
	}

}
