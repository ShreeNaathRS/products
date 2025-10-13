package com.swiftcart.products.lambda.function;

import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.swiftcart.products.dto.AuthDTO;
import com.swiftcart.products.dto.CustomUserDetails;
import com.swiftcart.products.dto.LoginRequest;
import com.swiftcart.products.entity.LoginUserEntity;
import com.swiftcart.products.service.LoginUserService;
import com.swiftcart.products.util.TokenUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationLambdaFunction {
	
	private final LoginUserService loginUserService;
	private final PasswordEncoder passwordEncoder;
	private final TokenUtil tokenUtil;

	@Bean
	public Function<LoginRequest, ResponseEntity<?>> generateToken() {
	    return request -> {
	        try {
	            LoginUserEntity userEntity = loginUserService.findByname(request.getUserName());
	            if (userEntity == null || !passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
	            	return new ResponseEntity<String>("You are not Authorized!!", null, HttpStatus.UNAUTHORIZED);
	            }
	            CustomUserDetails user = new CustomUserDetails(userEntity);
	            RSAPrivateKey privateKey = tokenUtil.loadPrivateKey("private_key.pem");
	            Algorithm algorithm = Algorithm.RSA256(privateKey);

	            String token = JWT.create()
	                .withSubject(user.getUsername())
	                .withExpiresAt(new Date(System.currentTimeMillis() + (60 * 60 * 1000)))
	                .withIssuer("https://dev-z98mxvin.us.auth0.com")
	                .withAudience("https://swiftcart/api")
	                .withClaim("roles", user.getAuthorities().stream()
	                    .map(GrantedAuthority::getAuthority)
	                    .collect(Collectors.toList()))
	                .sign(algorithm);

	            AuthDTO auth = new AuthDTO(
	                user.getUserId(),
	                user.getUsername(),
	                user.getEmail(),
	                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()),
	                token
	            );

	            return ResponseEntity.ok(auth);

	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Token generation failed");
	        }
	    };
	}

}
