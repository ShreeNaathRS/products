package com.swiftcart.products.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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
