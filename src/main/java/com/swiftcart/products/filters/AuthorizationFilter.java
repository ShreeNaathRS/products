package com.swiftcart.products.filters;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.swiftcart.products.constants.APIConstants;

public class AuthorizationFilter extends OncePerRequestFilter {
	
	private boolean isAllowedEndPoint(String method, String endpoint) {
		switch(method) {
			case "GET":
				return APIConstants.ALLOWED_GET_END_POINTS.stream()
						.anyMatch(allowedEndPoint->endpoint.startsWith(allowedEndPoint));
			case "POST":
				return APIConstants.ALLOWED_POST_END_POINTS.stream()
						.anyMatch(allowedEndPoint->endpoint.startsWith(allowedEndPoint));
			default:
				return false;
		}
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if(isAllowedEndPoint(request.getMethod(), request.getServletPath())) {
			filterChain.doFilter(request, response);
		}
		else {
			String authorizationHeader = request.getHeader("Authorization");
			if(authorizationHeader != null) {
				try {
					String token = authorizationHeader.substring("Bearer ".length());
					Algorithm algorithm = Algorithm.HMAC256("Shree-secretKey".getBytes());
					JWTVerifier verifier = JWT.require(algorithm).build();
					DecodedJWT decodedJWT = verifier.verify(token);
					String userName = decodedJWT.getSubject();
					String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, null,Stream.of(roles).map(role->new SimpleGrantedAuthority(role)).collect(Collectors.toList()));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					filterChain.doFilter(request, response);					
				}
				catch(TokenExpiredException e) {
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.getWriter().write("Token has Expired");
					
				}
				
			}
			else {
				response.setStatus(HttpStatus.FORBIDDEN.value());
				response.getWriter().write("No token found in the header");
			}

		}

		
	}

}
