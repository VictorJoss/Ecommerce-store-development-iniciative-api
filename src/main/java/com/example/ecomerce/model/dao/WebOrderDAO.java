package com.example.ecomerce.model.dao;

import com.example.ecomerce.model.LocalUser;
import com.example.ecomerce.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface WebOrderDAO extends ListCrudRepository<WebOrder, Long> {
    List<WebOrder> findByUser(LocalUser user);
}
