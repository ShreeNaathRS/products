package com.swiftcart.products.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.swiftcart.products.dto.ErrorResponse;

@Service
public class ResponseEntityUtil {
	
	public <T> ResponseEntity<String> getErrorResponseEntity(ErrorResponse error) {
		String body = "There was an exception in "+error.getMethod().toString()+":"+error.getEndPoint()+" at "+error.getName().getSimpleName();
		return new ResponseEntity<String>(body, null, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
