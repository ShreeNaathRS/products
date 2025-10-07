package com.swiftcart.products.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator ="seq_product_id" )
	@SequenceGenerator(name = "seq_product_id",initialValue = 1,allocationSize = 1)
	@Column(name = "prod_id")
	private Long id;
	
	@Column(name = "prod_catg_id")
	private Long category;

	@Column(name = "prod_sub_catg_id")
	private Long subCategory;
	
	@Column(name = "prod_company")
	private String company;
	
	@Column(name = "prod_description")
	private String desc;
	
	@Column(name = "prod_price")
	private Double price;
	
	@Column(name = "prod_image")
	private String imageUrl;

}
