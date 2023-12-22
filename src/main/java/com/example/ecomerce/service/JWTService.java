package com.example.ecomerce.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.ecomerce.model.LocalUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

//Maneja y genera los tokens de autenticacion
@Service
public class JWTService {

    //Llave para encriptar el token con el algoritmo HMAC256
    @Value("${jwt.algorithm.key}")
    private String algorithmKey;
    //Emisor del token
    @Value("${jwt.issuer}")
    private String issuer;
    //Tiempo de expiracion del token
    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;
    //Algoritmo de encriptacion
    private Algorithm algorithm;
    //Llave para obtener el username del token
    private static final String USERNAME_KEY = "USERNAME";
    private static final String EMAIL_KEY = "EMAIL";

    //Se ejecuta despues de que se inyectan las dependencias
    @PostConstruct
    public void postConstruct(){
        //Se crea el algoritmo de encriptacion
        algorithm = Algorithm.HMAC256(algorithmKey);
    }

    //Genera un token de autenticacion
    public String generateJWT(LocalUser user){
        //Se crea el token
        return JWT.create()
                    //Se le agrega el username
                .withClaim(USERNAME_KEY, user.getUsername())
                //Se le agrega el tiempo de expiracion
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
                //Se le agrega el emisor
                .withIssuer(issuer)
                //Se firma con el algoritmo
                .sign(algorithm);
    }

    //genera un token de verificacion de email
    public String generateVerificationJWT(LocalUser user){
        return JWT.create()
                //Se le agrega el username
                .withClaim(EMAIL_KEY, user.getEmail())
                //Se le agrega el tiempo de expiracion
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
                //Se le agrega el emisor
                .withIssuer(issuer)
                //Se firma con el algoritmo
                .sign(algorithm);
    }

    //Obtiene el username del token
    public String getUsername(String token){
        DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
        return jwt.getClaim(USERNAME_KEY).asString();
    }

}
