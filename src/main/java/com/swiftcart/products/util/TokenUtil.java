package com.swiftcart.products.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.swiftcart.products.entity.LoginUserEntity;
import com.swiftcart.products.repo.LoginUserRepo;

@Service
public class TokenUtil {
	
	@Autowired
	private LoginUserRepo loginUserRepo;
	
	public LoginUserEntity getLoggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userName = (String)authentication.getPrincipal();
		return loginUserRepo.findByname(userName);
	}
	
}
