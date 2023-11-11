package com.example.ecomerce.model.dao;

import com.example.ecomerce.model.LocalUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

//Maneja la tabla de usuarios
public interface LocalUserDao extends ListCrudRepository<LocalUser, Long> {

    //Verifica si el username ya existe
    Optional<LocalUser> findByUsernameIgnoreCase(String username);

    //Verifica si el email ya existe
    Optional<LocalUser> findByEmailIgnoreCase(String email);

}
