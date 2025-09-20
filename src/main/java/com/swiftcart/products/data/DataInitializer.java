package com.swiftcart.products.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.swiftcart.products.entity.CategoryEntity;
import com.swiftcart.products.entity.LoginRoleEntity;
import com.swiftcart.products.entity.LoginUserEntity;
import com.swiftcart.products.entity.ProductEntity;
import com.swiftcart.products.entity.SubCategoryEntity;
import com.swiftcart.products.repo.LoginUserRepo;
import com.swiftcart.products.repo.ProductRepo;
import com.swiftcart.products.repo.SubCategoryRepo;
import com.swiftcart.products.service.CategoryService;
import com.swiftcart.products.service.LoginRoleService;
import com.swiftcart.products.service.LoginUserService;
import com.swiftcart.products.service.ProductService;
import com.swiftcart.products.service.SubCategoryService;

@Component
public class DataInitializer implements CommandLineRunner{
	
	@Autowired
	LoginUserService loginUserService;
	
	@Autowired
	LoginRoleService loginRoleService;
	
	@Autowired
	LoginUserRepo loginUserRepo;
	
	@Autowired
	SubCategoryRepo subCategoryRepo;
	
	@Autowired
	SubCategoryService subCategoryService;
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	ProductRepo productrepo;
	
	@Override
	public void run(String... str) throws Exception {
		if(loginUserRepo.count() == 0) {
			LoginRoleEntity adminRole = new LoginRoleEntity();
			adminRole.setName("ADMIN");
			loginRoleService.saveRole(adminRole);
			LoginRoleEntity normalRole = new LoginRoleEntity();
			normalRole.setName("NORMAL");
			loginRoleService.saveRole(normalRole);
			LoginUserEntity userEntity = new LoginUserEntity();
			List<LoginRoleEntity> adminRoleList = new ArrayList<>();
			adminRoleList.add(adminRole);
			userEntity.setName("Shree");
			userEntity.setPassword("shree");
			userEntity.setEmail("shree@gmail.com");
			userEntity.setRoles(adminRoleList);
			loginUserService.createUser(userEntity);
			LoginUserEntity normalUserEntity = new LoginUserEntity();
			List<LoginRoleEntity> normalRoleList = new ArrayList<>();
			normalRoleList.add(normalRole);
			normalUserEntity.setName("Ramya");
			normalUserEntity.setPassword("ramya");
			normalUserEntity.setEmail("ramya@gmail.com");
			normalUserEntity.setRoles(normalRoleList);
			loginUserService.createUser(normalUserEntity);
		}
		if(subCategoryRepo.count()==0) {
			CategoryEntity furniture = new CategoryEntity();
			furniture.setName("Furniture");
			categoryService.createCategory(furniture);
			SubCategoryEntity mattress = new SubCategoryEntity(null, "Mattresses", "/mattress.jpg", furniture);
			subCategoryService.createSubCategory(mattress);
			SubCategoryEntity beanBag = new SubCategoryEntity(null, "Bean Bags", "/bean-bag.jpg", furniture);
			subCategoryService.createSubCategory(beanBag);
			SubCategoryEntity shoeRack = new SubCategoryEntity(null, "Shoe Racks", "/shoe-rack.jpg", furniture);
			subCategoryService.createSubCategory(shoeRack);
		}
		if(productrepo.count()==0) {
			ProductEntity nilkamal = new ProductEntity();
			nilkamal.setCategory(1L);
			nilkamal.setCompany("Nilkamal");
			nilkamal.setDesc("SLEEP Lite Dual Comfort 5 Inch Mattress | 10 Year Warranty");
			nilkamal.setImageUrl("/nilkamal.jpg");
			nilkamal.setPrice(4569.00);
			nilkamal.setSubCategory(1L);
			productService.createProduct(nilkamal);
			ProductEntity livpure = new ProductEntity();
			livpure.setCategory(1L);
			livpure.setCompany("Livpure");
			livpure.setDesc("Smart Lumbar HR Foam Mattress| 3D Sleeptech Zones");
			livpure.setImageUrl("/livpure.jpg");
			livpure.setPrice(4669.00);
			livpure.setSubCategory(1L);
			productService.createProduct(livpure);
		}
		
	}

}
