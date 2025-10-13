package com.swiftcart.products.lambda.function;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Component
public class PaymentLambdaFunction {

	@Bean
	public Function<Map<String, Object>, ResponseEntity<?>> createPayment() {
		return data -> {
			try {
				int amount = (int) data.get("amount");

				RazorpayClient razorpay = new RazorpayClient("rzp_test_RGE8UNieYQNDVG", "Ns6NFX2iPXkGMBaf5oArUXbr");

				JSONObject orderRequest = new JSONObject();
				orderRequest.put("amount", amount * 100);
				orderRequest.put("currency", "INR");
				orderRequest.put("receipt", "order_rcptid_" + System.currentTimeMillis());

				Order order = razorpay.orders.create(orderRequest);

				Map<String, Object> response = new HashMap<>();
				response.put("id", order.get("id"));
				response.put("amount", order.get("amount"));
				response.put("currency", order.get("currency"));

				return ResponseEntity.ok(response);
			} catch (RazorpayException e) {
				e.printStackTrace();
				return null;
			}
		};
	}
}
