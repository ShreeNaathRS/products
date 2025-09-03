package com.swiftcart.products.entity;


import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="login_users",uniqueConstraints = { @UniqueConstraint(name="uk_user_id",columnNames = "user_name") })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_user_id")
	@SequenceGenerator(name = "seq_user_id",initialValue = 1,allocationSize = 1)
	@Column(name = "user_id")
	private Long id;
	
	@Column(name = "user_name")
	private String name;
	
	@Column(name = "user_password")
	private String password;
	
	@Column(name="user_email")
	private String email;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "login_user_roles",joinColumns = {@JoinColumn(name="user_id")},inverseJoinColumns = {@JoinColumn(name="role_id")})
	private List<LoginRoleEntity> roles;
	
}
