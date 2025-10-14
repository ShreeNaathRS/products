package com.swiftcart.products.lambda.function;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.context.ConfigurableApplicationContext;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.swiftcart.products.ProductsApplication;

public class LambdaLocalRunner {

    public static void main(String[] args) {
        try (ConfigurableApplicationContext context = SpringApplication.run(ProductsApplication.class)) {

            @SuppressWarnings("unchecked")
            Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> function =
                    (Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>) context
                            .getBean(FunctionCatalog.class)
                            .lookup("cartController");

            APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
            request.setHttpMethod("GET"); // or GET, PUT, DELETE
            request.setPath("/cart");
            request.setBody("{}");

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJTaHJlZSIsImF1ZCI6Imh0dHBzOi8vc3dpZnRjYXJ0L2FwaSIsInJvbGVzIjpbIkFETUlOIl0sImlzcyI6Imh0dHBzOi8vZGV2LXo5OG14dmluLnVzLmF1dGgwLmNvbSIsImV4cCI6MTc2MDQ2MjIwNn0.nutPLc2lrlvTHxuxFshbfCDelPY9hyTcOK2rgRE46IuRhWud_4q02_0wVroj_z6Kk-lmNs9zDEh3JwDeS95x7ywvuTlK44nxs9A2EcX7VF5xWEX8IQs7rB7hyJA64mgiVY76BUa6WxiQQWDfiuhnw1HD1Rb3rwd_WefcwhAtPHda1Q6jS6xCGM5XwmrRGYxnXne8XS4KZGidcKpwODM4rB1ZpTLrQ1DmCif1P4evnUmR3HyvFTqzMbNFQ8T0Q3ig3NDUO20Yhm5bjZpPDrmQVXCwpFoMW5U6v29jEA9mtveqVe8qhrGT3cRpwKSX8qrbwqkS-IH9VAglQi1NqHWqoA");
            request.setHeaders(headers);

            APIGatewayProxyResponseEvent response = function.apply(request);

            System.out.println("Status: " + response.getStatusCode());
            System.out.println("Body: " + response.getBody());
        }
    }
}
