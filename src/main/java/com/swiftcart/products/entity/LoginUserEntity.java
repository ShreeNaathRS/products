package com.swiftcart.products.entity;


import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
