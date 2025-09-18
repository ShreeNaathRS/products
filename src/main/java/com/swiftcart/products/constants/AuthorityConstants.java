package com.swiftcart.products.constants;

public class AuthorityConstants {
	public static final String ADMIN_AUTHORITY = "hasAuthority('ADMIN')";
	public static final String NORMAL_AUTHORITY = "hasAuthority('NORMAL')";
	public static final String ADMIN_OR_NORMAL_AUTHORITY = "hasAuthority('NORMAL') || hasAuthority('ADMIN')";
}
