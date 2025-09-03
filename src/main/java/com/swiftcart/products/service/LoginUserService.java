package com.swiftcart.products.service;

import com.swiftcart.products.entity.LoginUserEntity;

public interface LoginUserService {
	
	Long createUser(LoginUserEntity user);

	LoginUserEntity getUser(Long id);

	boolean deleteUser(Long id);
	
	LoginUserEntity findByname(String name);

}
