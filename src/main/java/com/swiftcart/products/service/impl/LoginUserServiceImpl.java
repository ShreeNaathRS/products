package com.swiftcart.products.service.impl;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.swiftcart.products.dto.CustomUserDetails;
import com.swiftcart.products.entity.LoginUserEntity;
import com.swiftcart.products.repo.LoginUserRepo;
import com.swiftcart.products.service.LoginUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginUserServiceImpl implements LoginUserService, UserDetailsService {
	
	
	private final LoginUserRepo loginUserRepo;
	
	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		LoginUserEntity user = loginUserRepo.findByname(username);
//		if (user == null) {
//			throw new UsernameNotFoundException("User not present in db");
//		}
//		Collection<SimpleGrantedAuthority> authorities = user.getRoles().stream()
//				.map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
//
//		return new User(user.getName(), user.getPassword(), authorities);
		LoginUserEntity user = loginUserRepo.findByname(username);
	    if (user == null) {
	        throw new UsernameNotFoundException("User not present in db");
	    }
	    return new CustomUserDetails(user);
	}
	
	
	
	@Override
	public Long createUser(LoginUserEntity user) {
		user.setPassword(passwordEncoder().encode(user.getPassword()));
		LoginUserEntity userEntity= loginUserRepo.save(user);
		return userEntity.getId();
	}
	
	@Override
	public LoginUserEntity getUser(Long id) {
		Optional<LoginUserEntity> user = loginUserRepo.findById(id);
		return user.get();
		
	}
	
	@Override
	public boolean deleteUser(Long id) {
		LoginUserEntity user = getUser(id);
		if(user != null) {
			loginUserRepo.deleteById(id);
			return true;
		}		
		return false;
	}
	
	@Override
	public LoginUserEntity findByname(String name) {	
		return loginUserRepo.findByname(name);
	}

}
