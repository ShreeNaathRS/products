package com.swiftcart.products.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swiftcart.products.entity.LoginUserEntity;
import com.swiftcart.products.service.LoginUserService;

@RestController
@RequestMapping("/login")
//@PreAuthorize("hasAuthority('ADMIN')")
public class LoginUserController {

	@Autowired
	private LoginUserService userService;

	@PostMapping()
	public ResponseEntity<?> createUser(@RequestBody LoginUserEntity userEntity) {
		try {			
			Long userId = userService.createUser(userEntity);
			return new ResponseEntity<Long>(userId, null, HttpStatus.CREATED);
		} catch(DataIntegrityViolationException ex) {
	        return new ResponseEntity<>("User already exists", HttpStatus.CONFLICT);
		}
	}

	@GetMapping(path = "/{id}")
	public ResponseEntity<LoginUserEntity> getUser(@PathVariable Long id) {
		LoginUserEntity entity = userService.getUser(id);
		return new ResponseEntity<LoginUserEntity>(entity, null, HttpStatus.OK);
	}

	@DeleteMapping(path = "/{id}")
	public ResponseEntity<Boolean> deleteUser(@PathVariable Long id) {
		return new ResponseEntity<Boolean>(userService.deleteUser(id), null, HttpStatus.OK);
	}

}
