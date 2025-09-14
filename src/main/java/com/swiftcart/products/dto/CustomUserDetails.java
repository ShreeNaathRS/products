package com.swiftcart.products.dto;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.swiftcart.products.entity.LoginUserEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomUserDetails extends User {
    private Long userId;
    private String email;

    public CustomUserDetails(LoginUserEntity user) {
        super(user.getName(), user.getPassword(),
              user.getRoles().stream()
                  .map(role -> new SimpleGrantedAuthority(role.getName()))
                  .collect(Collectors.toList()));
        this.userId = user.getId();
        this.email = user.getEmail();
    }
}