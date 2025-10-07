package com.swiftcart.products.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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
