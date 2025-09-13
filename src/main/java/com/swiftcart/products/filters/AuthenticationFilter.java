package com.swiftcart.products.filters;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftcart.products.dto.AuthDTO;
import com.swiftcart.products.dto.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	
	private final AuthenticationManager authenticationManager;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String userName = request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
		String password = request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);
		return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		CustomUserDetails user = (CustomUserDetails) authResult.getPrincipal();
		Long userId = user.getUserId();
		Algorithm algorithm = Algorithm.HMAC256("Shree-secretKey".getBytes());
		String token = JWT.create().withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + (4*60 * 60 * 1000)))
				.withIssuer(request.getRequestURL().toString())
				.withClaim("roles",
						user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.sign(algorithm);
		AuthDTO auth = new AuthDTO(userId, user.getUsername(),user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()), token);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.OK.value());
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.writeValue(response.getWriter(), auth);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.getWriter().write("Unsuccessful Authentication");
		response.getWriter().flush();
	}
}
