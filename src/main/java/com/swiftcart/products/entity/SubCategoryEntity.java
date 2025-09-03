package com.swiftcart.products.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
