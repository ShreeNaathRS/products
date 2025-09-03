package com.swiftcart.products.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swiftcart.products.entity.LoginRoleEntity;
import com.swiftcart.products.repo.LoginRoleRepo;
import com.swiftcart.products.service.LoginRoleService;

@Service
public class LoginRoleServiceImpl implements LoginRoleService{
	
	@Autowired
	LoginRoleRepo loginRoleRepo;
	
	@Override
	public LoginRoleEntity saveRole(LoginRoleEntity roleEntity) {
		return loginRoleRepo.save(roleEntity);
	}
}
