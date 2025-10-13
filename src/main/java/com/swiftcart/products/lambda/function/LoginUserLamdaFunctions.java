package com.swiftcart.products.lambda.function;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.swiftcart.products.entity.LoginUserEntity;
import com.swiftcart.products.service.LoginUserService;

@Component
public class LoginUserLamdaFunctions {

	private final LoginUserService userService;

	public LoginUserLamdaFunctions(LoginUserService userService) {
		this.userService = userService;
	}

	@Bean
	public Function<LoginUserEntity, ResponseEntity<?>> createUser() {
		return userEntity -> {
			try {
				Long userId = userService.createUser(userEntity);
				return new ResponseEntity<Long>(userId, null, HttpStatus.CREATED);
			} catch (DataIntegrityViolationException ex) {
				return new ResponseEntity<>("User already exists", HttpStatus.CONFLICT);
			}
		};
	}

	@Bean
	public Function<Long, ResponseEntity<LoginUserEntity>> getUser() {
		return id -> new ResponseEntity<LoginUserEntity>(userService.getUser(id), null, HttpStatus.OK);
	}

	@Bean
	public Function<Long, ResponseEntity<Boolean>> deleteUser() {
		return id -> new ResponseEntity<Boolean>(userService.deleteUser(id), null, HttpStatus.OK);
	}
}
