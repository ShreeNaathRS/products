package com.swiftcart.products.lambda.function;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import com.swiftcart.products.ProductsApplication;

public class LambdaLocalRunner {

    public static void main(String[] args) {
        try (ConfigurableApplicationContext context = SpringApplication.run(ProductsApplication.class)) {

            @SuppressWarnings("unchecked")
            Function<Message<Object>, ResponseEntity<?>> function =
                    (Function<Message<Object>, ResponseEntity<?>>) context
                            .getBean(FunctionCatalog.class)
                            .lookup("getCart");

            Map<String, Object> headerMap = new HashMap<>();
            headerMap.put("content-type", "application/json");
            headerMap.put("authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJTaHJlZSIsImF1ZCI6Imh0dHBzOi8vc3dpZnRjYXJ0L2FwaSIsInJvbGVzIjpbIkFETUlOIl0sImlzcyI6Imh0dHBzOi8vZGV2LXo5OG14dmluLnVzLmF1dGgwLmNvbSIsImV4cCI6MTc2MDQ0MTI3MH0.sQh-Y7QDwAm0FcNxFpq4c1n2gOUnYnUXZdPV7tNIjGc6R5nfsx24k3r6qC_NBJtCMPPn5MWm6FOWgvxEA3Cr7wMlzYJ3oVkJmRhF08NhqjrtxNlSQHllIGG_xeG3Q7y6JtjMUZp6d7vud5qgcohl-U6HLQqN9Q6_4irC1V_48xwtqBLjswjrhUSnTKqIuzGTqT_lLZCxSVGPlOZl4-3_npwXGjEJr04U62RI8_dklhRHc0czjTxyHLs8NAybM7QxwIi9T6Ek7FCF--WfWYoF3YbeFdjHLyT6jpvuuco9ecgwCf5WDNX89mWiadCSeR-XbzN2hPeYPj2EjnG2OqpNSg");
            MessageHeaders messageHeaders = new MessageHeaders(headerMap);
            Message<Object> message = MessageBuilder.createMessage("{}", messageHeaders);

            ResponseEntity<?> result = function.apply(message);

            System.out.println("Status: " + result.getStatusCode());
            System.out.println("Body: " + result.getBody());
        }
    }
}
