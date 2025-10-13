package com.swiftcart.products.util;

import org.springframework.stereotype.Component;

import com.swiftcart.products.entity.LoginUserEntity;

@Component
public class RequestContext {
	private static final ThreadLocal<LoginUserEntity> currentUser = new ThreadLocal<>();

	public static void setUser(LoginUserEntity user) {
		currentUser.set(user);
	}

	public static LoginUserEntity getUser() {
		return currentUser.get();
	}

	public static void clear() {
		currentUser.remove();
	}
}
