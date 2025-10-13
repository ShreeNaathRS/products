package com.swiftcart.products.filters;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftcart.products.dto.AuthDTO;
import com.swiftcart.products.dto.CustomUserDetails;
import com.swiftcart.products.util.TokenUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final TokenUtil tokenUtil;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String userName = request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
		String password = request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);
		return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
	}
	
	private void setSecurityContext(CustomUserDetails user) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUserId(), user.getPassword(), user.getAuthorities());
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		CustomUserDetails user = (CustomUserDetails) authResult.getPrincipal();
		Long userId = user.getUserId();
		RSAPrivateKey privateKey;
		try {
			privateKey = tokenUtil.loadPrivateKey("private_key.pem");
			Algorithm algorithm = Algorithm.RSA256(privateKey);

			String token = JWT.create()
			    .withSubject(user.getUsername())
			    .withExpiresAt(new Date(System.currentTimeMillis() + ( 60 * 60 * 1000)))
			    .withIssuer("https://dev-z98mxvin.us.auth0.com")
			    .withAudience("https://swiftcart/api")
			    .withClaim("roles",
			        user.getAuthorities().stream()
			            .map(GrantedAuthority::getAuthority)
			            .collect(Collectors.toList()))
			    .sign(algorithm);

			setSecurityContext(user);
			AuthDTO auth = new AuthDTO(userId, user.getUsername(),user.getEmail(), user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()), token);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setStatus(HttpStatus.OK.value());
		    ObjectMapper mapper = new ObjectMapper();
		    mapper.writeValue(response.getWriter(), auth);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.getWriter().write("Unsuccessful Authentication");
		response.getWriter().flush();
	}
}
