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
@Table(name="login_roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRoleEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator ="seq_role_id" )
	@SequenceGenerator(name = "seq_role_id",initialValue = 1,allocationSize = 1)
	@Column(name = "role_id")
	private Long id;
	
	@Column(name = "role_name")
	private String name;

}
