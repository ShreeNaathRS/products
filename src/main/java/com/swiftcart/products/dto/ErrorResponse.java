package com.swiftcart.products.dto;

import org.springframework.web.bind.annotation.RequestMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
	private Class<?> name;
	private RequestMethod method;
	private String endPoint;
}
