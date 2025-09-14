package com.swiftcart.products.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthDTO {
	Long userId;
	String name;
	String email;
	List<String> roles;
	String token;
}
