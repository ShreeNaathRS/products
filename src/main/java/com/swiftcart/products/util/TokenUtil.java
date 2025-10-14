package com.swiftcart.products.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.swiftcart.products.entity.LoginUserEntity;
import com.swiftcart.products.repo.LoginUserRepo;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class TokenUtil {
	
	@Autowired
	private LoginUserRepo loginUserRepo;
	
	private HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attrs != null) ? attrs.getRequest() : null;
    }
	
	public String getHeader(String name) {
        HttpServletRequest request = getRequest();
        return (request != null) ? request.getHeader(name) : null;
    }
	
	public String getAuthorizationToken() {
		return getHeader("Authorization");
	}
	
	public RSAPrivateKey loadPrivateKey(String filename) throws Exception {
	    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
	    if (inputStream == null) {
	        throw new FileNotFoundException("Resource not found: " + filename);
	    }

	    // Read the key content
	    String key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
	    key = key.replace("-----BEGIN PRIVATE KEY-----", "")
	             .replace("-----END PRIVATE KEY-----", "")
	             .replaceAll("\\s", "");

	    // Decode and generate RSA key
	    byte[] decoded = Base64.getDecoder().decode(key);
	    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    return (RSAPrivateKey) kf.generatePrivate(spec);
	}
	
	private RSAPublicKey loadPublicKey(String filename) {
		try {			
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
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch(InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public LoginUserEntity getLoggedInUserFromContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userName = (String)authentication.getPrincipal();
		return loginUserRepo.findByname(userName);
	}
	
	public LoginUserEntity getLoggedInUserFromToken(String token) {
		DecodedJWT decodedToken = verifyToken(token);
		return verifyToken(token) == null? null: loginUserRepo.findByname(decodedToken.getSubject());
	}
	
	public LoginUserEntity getLoggedInUserFromCustomContext() {
		return RequestContext.getUser();
//		
//		
//		
//		
//		return getLoggedInUserFromContext();
	}
	
	public boolean hasAuthority(DecodedJWT token, String requiredAuthority){
		String[] userRoles = token.getClaim("roles").asArray(String.class);
		List<String> requiredAuthorityList = Arrays.asList(requiredAuthority.split(","));
		return Arrays.asList(userRoles).stream().anyMatch(role-> requiredAuthorityList.stream().anyMatch(authority->authority.equals(role)));
	}
	
	public DecodedJWT verifyToken(String token){
        return getDecodedToken(token);
	}
	
	public DecodedJWT verifyToken(String token, String requiredAuthority) {
		DecodedJWT decodedToken = getDecodedToken(token);
		Boolean userHasAuthority = hasAuthority(decodedToken, requiredAuthority);
		if(!userHasAuthority) {
			throw new AccessDeniedException("You do not have access!!");
		}
		return decodedToken;
	}
	
	private DecodedJWT getDecodedToken(String token) {
		try {			
			RSAPublicKey publicKey = loadPublicKey("public_key.pem");
			Algorithm algorithm = Algorithm.RSA256(publicKey, null);
			JWTVerifier verifier = JWT.require(algorithm)
					.withIssuer("https://dev-z98mxvin.us.auth0.com")
					.withAudience("https://swiftcart/api")
					.build();
			return verifier.verify(token);
		} catch(AccessDeniedException e) {
			throw new AccessDeniedException(e.getMessage());
		} catch (TokenExpiredException e) {
	        throw new TokenExpiredException("Your token has expired!!", null);
	    } catch (JWTVerificationException e) {
	        throw new JWTVerificationException("Token verification failed!!");
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Internal Server Error!! Please contact admin");
	    }
	}

	public <T> Function<Message<T>, ResponseEntity<?>> authorized(Function<T, ResponseEntity<?>> handler) {
        return message -> {
            try {
            	String token = (String) message.getHeaders().get("Authorization");
            	DecodedJWT jwt = verifyToken(token);
	            LoginUserEntity user = loginUserRepo.findByname(jwt.getSubject());
	            RequestContext.setUser(user);
                return handler.apply(message.getPayload());
            } catch (JWTVerificationException | AccessDeniedException e) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	        }
        };
    }
	
	public <T> Function<Message<T>, ?> authorized(Function<T, ResponseEntity<?>> handler, String requiredAuthority) {
	    return message -> {
	        try {
	            if (message == null || message.getHeaders() == null) {
	                return "Missing message or headers";
	            }

	            Message<T> normalizedMessage = getNormalizedMessage(message);
	            Object contentTypeHeader = normalizedMessage.getHeaders().get("Content-Type");
	            if (contentTypeHeader == null) {
	                return "Missing 'Content-Type' header";
	            }

	            String contentType = contentTypeHeader.toString();
	            if (!contentType.equalsIgnoreCase("application/json")) {
	                return "Unsupported Content-Type: " + contentType;
	            }

	            String token = (String) normalizedMessage.getHeaders().get("Authorization");
	            DecodedJWT jwt = verifyToken(token, requiredAuthority);
	            LoginUserEntity user = loginUserRepo.findByname(jwt.getSubject());
	            RequestContext.setUser(user);

	            ResponseEntity<?> response = handler.apply(normalizedMessage.getPayload());
	            return response.getBody();

	        } catch (JWTVerificationException | AccessDeniedException e) {
	            return e.getMessage();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "Unexpected error: " + e.getMessage();
	        }
	    };
	}
	
	private <T> Message<T> getNormalizedMessage(Message<T> message) {
		Map<String, Object> normalizedHeaders = new HashMap<>();
    	String bearerToken = (String)(null != message.getHeaders().get("authorization")? message.getHeaders().get("authorization"): null != message.getHeaders().get("Authorization")? null != message.getHeaders().get("Authorization"): null);
    	normalizedHeaders.put("Content-Type", "application/json");
    	normalizedHeaders.put("Authorization", bearerToken == null? null: bearerToken.substring(7));
        Message<T> normalizedMessage = MessageBuilder.createMessage(
        		message.getPayload(),
                new MessageHeaders(normalizedHeaders)
        );
        return normalizedMessage;
	}
	
}
