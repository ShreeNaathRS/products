package com.swiftcart.products.constants;

import java.util.ArrayList;
import java.util.List;

public class APIConstants {
	public static final List<String> ALLOWED_GET_END_POINTS = new ArrayList<String>() {
		{
			add("/token");
			add("/category");
			add("/products");
		}
	};
	
	public static final List<String> ALLOWED_POST_END_POINTS = new ArrayList<String>() {
		{
			add("/login");
		}
	};
}
