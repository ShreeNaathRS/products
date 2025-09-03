package com.swiftcart.products.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="category")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator ="seq_category_id" )
	@SequenceGenerator(name = "seq_category_id",initialValue = 1,allocationSize = 1)
	@Column(name = "catg_id")
	private Long id;
	
	@Column(name = "catg_name")
	private String name;
	
	@OneToMany(mappedBy = "category",cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SubCategoryEntity> subCategories;

}
