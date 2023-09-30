package com.example.ecomerce.api.model;

//Body para el jwt de un usuario
public class LoginResponse {

    private String jwt;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
