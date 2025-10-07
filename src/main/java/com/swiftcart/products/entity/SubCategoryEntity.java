package com.swiftcart.products.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="sub_category")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubCategoryEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator ="seq_sub_category_id" )
	@SequenceGenerator(name = "seq_sub_category_id",initialValue = 1,allocationSize = 1)
	@Column(name = "sub_catg_id")
	private Long id;
	
	@Column(name = "sub_catg_name")
	private String name;
	
	@Column(name = "sub_catg_image")
	private String image;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "catg_id")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "subCategories"})
	private CategoryEntity category;

}
