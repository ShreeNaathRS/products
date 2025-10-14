package com.swiftcart.products.lambda.function;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftcart.products.dto.ErrorResponse;
import com.swiftcart.products.entity.CartEntity;
import com.swiftcart.products.service.CartService;
import com.swiftcart.products.util.RequestContext;
import com.swiftcart.products.util.ResponseEntityUtil;
import com.swiftcart.products.util.TokenUtil;

@Component
public class CartLambdaFunctions {

    private final CartService cartService;
    private final TokenUtil tokenUtil;
    private final ResponseEntityUtil responseEntityUtil;
    private final ObjectMapper objectMapper;

    public CartLambdaFunctions(CartService cartService, TokenUtil tokenUtil, ResponseEntityUtil responseEntityUtil, ObjectMapper objectMapper) {
        this.cartService = cartService;
        this.tokenUtil = tokenUtil;
        this.responseEntityUtil = responseEntityUtil;
        this.objectMapper = objectMapper;
    }

    @Bean
    public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> cartController() {
        return request -> {
            String method = request.getHttpMethod();
            try {
                switch (method) {
                    case "GET":
                    	System.out.println("Inside GET");
                        return getCart(request);
                    case "POST":
                        return createCart(request);
                    case "PUT":
                        return updateCart(request);
                    case "DELETE":
                        return deleteCart(request);
                    default:
                        return new APIGatewayProxyResponseEvent()
                                .withStatusCode(405)
                                .withBody("Method Not Allowed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(500)
                        .withBody("Unexpected error: " + e.getMessage());
            } finally {
                RequestContext.clear();
            }
        };
    }

    private APIGatewayProxyResponseEvent createCart(APIGatewayProxyRequestEvent request) {
        return tokenUtil.authorized(CartEntity.class, (CartEntity cart) -> {
            try {
                Object result = cartService.createCart(cart);
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            } catch (Exception e) {
                e.printStackTrace();
                return responseEntityUtil.getErrorResponseEntity(
                        new ErrorResponse(CartEntity.class, RequestMethod.POST, "createCart"));
            }
        }, "ADMIN,NORMAL").apply(request);
    }

    private APIGatewayProxyResponseEvent updateCart(APIGatewayProxyRequestEvent request) {
        return tokenUtil.authorized(CartEntity.class, (CartEntity cart) -> {
            try {
                Object result = cartService.updateCart(cart);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                e.printStackTrace();
                return responseEntityUtil.getErrorResponseEntity(
                        new ErrorResponse(CartEntity.class, RequestMethod.PUT, "updateCart"));
            }
        }, "ADMIN,NORMAL").apply(request);
    }

    private APIGatewayProxyResponseEvent getCart(APIGatewayProxyRequestEvent request) {
        return tokenUtil.authorized(Object.class, (Object ignored) -> {
            try {
                Object result = cartService.getCart();
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                e.printStackTrace();
                return responseEntityUtil.getErrorResponseEntity(
                        new ErrorResponse(CartEntity.class, RequestMethod.GET, "getCart"));
            }
        }, "ADMIN,NORMAL").apply(request);
    }

    private APIGatewayProxyResponseEvent deleteCart(APIGatewayProxyRequestEvent request) {
        return tokenUtil.authorized(Object.class, (Object ignored) -> {
            try {
                Long deletedId = cartService.deleteCart();
                return ResponseEntity.ok(deletedId);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0L);
            }
        }, "ADMIN,NORMAL").apply(request);
    }

    private APIGatewayProxyResponseEvent buildResponse(Object body, HttpStatus status) {
        try {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(status.value())
                    .withBody(objectMapper.writeValueAsString(body));
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody("Serialization error: " + e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent buildError(Class<?> clazz, RequestMethod method, String action) {
        ErrorResponse error = new ErrorResponse(clazz, method, action);
        ResponseEntity<?> responseEntity = responseEntityUtil.getErrorResponseEntity(error);
        return buildResponse(responseEntity.getBody(), HttpStatus.valueOf(responseEntity.getStatusCodeValue()));
    }
}
