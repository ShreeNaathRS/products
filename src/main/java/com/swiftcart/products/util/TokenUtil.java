package com.swiftcart.products.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
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
		return isTokenExpired(decodedToken) || verifyToken(token) == null? null: loginUserRepo.findByname(decodedToken.getSubject());
	}
	
	public LoginUserEntity getLoggedInUserFromCustomContext() {
		return RequestContext.getUser();
	}
	
	public boolean isTokenExpired(DecodedJWT token) {
		Date tokenExpiryDate = token.getExpiresAt();
		return tokenExpiryDate.before(new Date());
	}
	
	public DecodedJWT verifyToken(String token) throws TokenExpiredException {
	    try {
	        // Decode token to extract Key ID (kid)
	        DecodedJWT decodedJWT = JWT.decode(token);
	        String keyId = decodedJWT.getKeyId();

	        // Fetch public key from Auth0 JWKS endpoint
	        JwkProvider provider = new UrlJwkProvider(new URL("https://dev-z98mxvin.us.auth0.com/.well-known/jwks.json"));
	        Jwk jwk = provider.get(keyId);
	        RSAPublicKey publicKey = (RSAPublicKey) jwk.getPublicKey();

	        // Verify token using Auth0's public key
	        Algorithm algorithm = Algorithm.RSA256(publicKey, null);
	        JWTVerifier verifier = JWT.require(algorithm)
	                .withIssuer("https://dev-z98mxvin.us.auth0.com/")
	                .withAudience("https://swiftcart/api")
	                .build();

	        return verifier.verify(token);

	    } catch (TokenExpiredException e) {
	        throw new TokenExpiredException("The token has expired!!", null);
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
            	LoginUserEntity user = getLoggedInUserFromToken(token);
            	RequestContext.setUser(user);
                verifyToken(token);
                return handler.apply(message.getPayload());
            } catch(TokenExpiredException e) {
            	return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    		} catch(JWTVerificationException e ) {
    			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    		} catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        };
    }
	
	public <T> Function<Message<T>, ResponseEntity<?>> authorized(Function<T, ResponseEntity<?>> handler, String requiredScope) {
	    return message -> {
	        try {
	            String token = (String) message.getHeaders().get("Authorization");
	            DecodedJWT jwt = verifyToken(token);

	            if (!hasRequiredScope(jwt, requiredScope)) {
	                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Missing required scope: " + requiredScope);
	            }

	            LoginUserEntity user = loginUserRepo.findByname(jwt.getSubject());
	            RequestContext.setUser(user);

	            return handler.apply(message.getPayload());

	        } catch (TokenExpiredException e) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
	        } catch (JWTVerificationException e) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	        }
	    };
	}

	
	private boolean hasRequiredScope(DecodedJWT jwt, String requiredScope) {
	    String scopeClaim = jwt.getClaim("scope").asString();
	    if (scopeClaim == null) return false;
	    String[] scopes = scopeClaim.split(" ");
	    for (String scope : scopes) {
	        if (scope.equals(requiredScope)) return true;
	    }
	    return false;
	}

	
}
