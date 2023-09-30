package com.example.ecomerce.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

//Encripta y verifica contraseñas
@Service
public class EncryptionService {

    //Numero de veces que se aplica el algoritmo de encriptacion
    @Value("${encryption.salt.rounds}")
    private int saltRounds;
    //Salto para encriptar
    private String salt;

    //Se ejecuta despues de que se inyectan las dependencias
    @PostConstruct
    public void postConstruct(){
        salt = BCrypt.gensalt(saltRounds);
    }

    //Encripta la contraseña
    public String encryptPassword(String password){
        return BCrypt.hashpw(password, salt);
    }

    //Verifica la contraseña
    public boolean verifyPassword(String password, String hashedPassword){
        return BCrypt.checkpw(password, hashedPassword);
    }
}
