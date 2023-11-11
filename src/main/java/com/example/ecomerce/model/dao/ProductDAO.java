package com.example.ecomerce.model.dao;

import com.example.ecomerce.model.Product;
import org.springframework.data.repository.ListCrudRepository;

public interface ProductDAO extends ListCrudRepository<Product, Long> {
}
