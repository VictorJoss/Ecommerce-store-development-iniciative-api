package com.example.ecomerce.model.dao;

import com.example.ecomerce.model.LocalUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

/**
 * Data Access Object for the LocalUser data.
 */
public interface LocalUserDao extends ListCrudRepository<LocalUser, Long> {

    Optional<LocalUser> findByUsernameIgnoreCase(String username);

    Optional<LocalUser> findByEmailIgnoreCase(String email);

}
