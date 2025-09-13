package com.swiftcart.products.dto;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.swiftcart.products.entity.LoginUserEntity;

public class CustomUserDetails extends User {
    private Long userId;

    public CustomUserDetails(LoginUserEntity user) {
        super(user.getName(), user.getPassword(),
              user.getRoles().stream()
                  .map(role -> new SimpleGrantedAuthority(role.getName()))
                  .collect(Collectors.toList()));
        this.userId = user.getId();
    }

    public Long getUserId() {
        return userId;
    }
}