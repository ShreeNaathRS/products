package com.swiftcart.products.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_order_products_id")
    @SequenceGenerator(name = "seq_order_products_id", initialValue = 1, allocationSize = 1)
    @Column(name = "ord_prd_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ord_prd_ord_id", nullable = false, referencedColumnName = "ord_id")
    @JsonIgnore
    private OrdersEntity order;

    @ManyToOne
    @JoinColumn(name = "ord_prd_prod_id", nullable = false, referencedColumnName = "prod_id")
    private ProductEntity product;

    @Column(name = "ord_prd_qty", nullable = false)
    private Integer qty;

    @Column(name = "ord_prd_price", nullable = false)
    private Double amt;
}
