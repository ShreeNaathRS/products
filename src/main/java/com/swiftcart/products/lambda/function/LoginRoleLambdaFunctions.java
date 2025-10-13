package com.swiftcart.products.lambda.function;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.swiftcart.products.entity.LoginRoleEntity;
import com.swiftcart.products.service.LoginRoleService;

@Component
public class LoginRoleLambdaFunctions {
	
	private final LoginRoleService roleService;

	public LoginRoleLambdaFunctions(LoginRoleService roleService) {
		this.roleService=roleService;
	}
	
	@Bean
	public Function<LoginRoleEntity, ResponseEntity<LoginRoleEntity>> createRole() {
		return roleEntity -> new ResponseEntity<LoginRoleEntity>(roleService.saveRole(roleEntity), null, HttpStatus.CREATED);
	}

}
