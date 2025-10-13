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
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.swiftcart.products.entity.LoginUserEntity;
import com.swiftcart.products.repo.LoginUserRepo;

@Service
public class TokenUtil {
	
	@Autowired
	private LoginUserRepo loginUserRepo;
	
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
	}
	
	public boolean hasAuthority(DecodedJWT token, String requiredAuthority){
		String[] roles = token.getClaim("roles").asArray(String.class);
		return Arrays.asList(roles).stream().anyMatch(role->role.equalsIgnoreCase(requiredAuthority));
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
	public <T> Function<Message<T>, ResponseEntity<?>> authorized(Function<T, ResponseEntity<?>> handler, String requiredAuthority) {
	    return message -> {
	        try {
	            String token = (String) message.getHeaders().get("Authorization");
	            DecodedJWT jwt = verifyToken(token, requiredAuthority);
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
	
}
