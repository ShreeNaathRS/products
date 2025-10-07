package com.swiftcart.products.filters;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

	    if (isAllowedEndPoint(request.getMethod(), request.getServletPath())) {
	        filterChain.doFilter(request, response);
	        return;
	    }

	    String authorizationHeader = request.getHeader("Authorization");
	    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
	        try {
	            String token = authorizationHeader.substring("Bearer ".length());
	            RSAPublicKey publicKey = loadPublicKey("public_key.pem");
	            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
	            JWTVerifier verifier = JWT.require(algorithm)
	                    .withIssuer("https://dev-z98mxvin.us.auth0.com")
	                    .withAudience("https://swiftcart/api")
	                    .build();

	            DecodedJWT decodedJWT = verifier.verify(token);
	            String userName = decodedJWT.getSubject();
	            String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

	            UsernamePasswordAuthenticationToken authenticationToken =
	                    new UsernamePasswordAuthenticationToken(userName, null,
	                            Stream.of(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

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
	
	private RSAPublicKey loadPublicKey(String filename) throws Exception {
	    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
	    if (inputStream == null) {
	        throw new FileNotFoundException("Resource not found: " + filename);
	    }

	    String key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
	    key = key.replace("-----BEGIN PUBLIC KEY-----", "")
	             .replace("-----END PUBLIC KEY-----", "")
	             .replaceAll("\\s", "");

	    byte[] decoded = Base64.getDecoder().decode(key);
	    X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    return (RSAPublicKey) kf.generatePublic(spec);
	}



}
