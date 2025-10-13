package com.swiftcart.products.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.swiftcart.products.constants.APIConstants;
import com.swiftcart.products.util.TokenUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthorizationFilter extends OncePerRequestFilter {
	
	private final TokenUtil tokenUtil;
	
	public AuthorizationFilter(TokenUtil tokenUtil) {
		this.tokenUtil=tokenUtil;
	}
	
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

	    if (isAllowedEndPoint(request.getMethod(), request.getServletPath())) {
	        filterChain.doFilter(request, response);
	        return;
	    }

	    String authorizationHeader = request.getHeader("Authorization");
	    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
	        try {
	            String token = authorizationHeader.substring("Bearer ".length());
	            DecodedJWT decodedJWT = tokenUtil.verifyToken(token);
	            String userName = decodedJWT.getSubject();
	            String[] scopes = decodedJWT.getClaim("scope").asString().split(" ");
	            List<SimpleGrantedAuthority> authorities = Arrays.stream(scopes)
	                .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
	                .collect(Collectors.toList());

	            UsernamePasswordAuthenticationToken authenticationToken =
	                new UsernamePasswordAuthenticationToken(userName, null, authorities);

	            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	            filterChain.doFilter(request, response);

	        } catch (TokenExpiredException e) {
	            response.setStatus(HttpStatus.UNAUTHORIZED.value());
	            response.getWriter().write("Token has expired");
	        } catch (Exception e) {
	            response.setStatus(HttpStatus.FORBIDDEN.value());
	            response.getWriter().write("Invalid token");
	        }
	    } else {
	        response.setStatus(HttpStatus.FORBIDDEN.value());
	        response.getWriter().write("No token found in the header");
	    }
	}
	
}
