package com.swiftcart.products.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginatedResponse<T> {
    private List<T> data;
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;

    public PaginatedResponse(Page<T> pageData) {
        this.data = pageData.getContent();
        this.page = pageData.getNumber();
        this.size = pageData.getSize();
        this.totalItems = pageData.getTotalElements();
        this.totalPages = pageData.getTotalPages();
    }
}