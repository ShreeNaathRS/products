package com.swiftcart.products.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.products.entity.LoginRoleEntity;
import com.swiftcart.products.service.LoginRoleService;

@RestController
@RequestMapping("/login/role")
//@PreAuthorize("hasAuthority('ADMIN')")
public class LoginRoleController {

	@Autowired
	private LoginRoleService roleService;

	@PostMapping()
	public ResponseEntity<LoginRoleEntity> createRole(@RequestBody LoginRoleEntity roleEntity) {
		return new ResponseEntity<LoginRoleEntity>(roleService.saveRole(roleEntity), null, HttpStatus.CREATED);
	}

}
