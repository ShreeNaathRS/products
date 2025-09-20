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
@Table(name = "cart_products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartProductsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_cart_products_id")
    @SequenceGenerator(name = "seq_cart_products_id", initialValue = 1, allocationSize = 1)
    @Column(name = "cart_prod_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_prod_cart_id", nullable = false, referencedColumnName = "cart_id")
    @JsonIgnore
    private CartEntity cart;

    @ManyToOne
    @JoinColumn(name = "cart_prod_prod_id", nullable = false, referencedColumnName = "prod_id")
    private ProductEntity product;

    @Column(name = "cart_prod_qty", nullable = false)
    private Integer qty;

    @Column(name = "cart_prod_price", nullable = false)
    private Double price;
}
