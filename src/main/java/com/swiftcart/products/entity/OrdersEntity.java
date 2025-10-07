package com.swiftcart.products.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator ="seq_order_id" )
	@SequenceGenerator(name = "seq_order_id",initialValue = 1,allocationSize = 1)
	@Column(name = "ord_id")
	private Long id;
	
	@Column(name = "ord_user_id")
	private Long user;
	
	@Column(name = "ord_receipt_id")
	private String receiptId;

	@Column(name = "ord_payment_id")
	private String paymentId;
	
	@Column(name = "ord_signature")
	private String signature;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderProductsEntity> products = new ArrayList<>();
	
	@Column(name = "ord_amt")
	private Double amt;
	
	@Column(name = "ord_created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
	    createdAt = LocalDateTime.now();
	}

}
