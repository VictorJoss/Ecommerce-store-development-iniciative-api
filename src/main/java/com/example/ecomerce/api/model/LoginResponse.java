package com.example.ecomerce.api.model;

/**
 * The response object sent from login request.
 */
public class LoginResponse {

    /** The JWT token to be used for authentication. */
    private String jwt;
    /** Was the login process successful? */
    private boolean sucess;
    /** The reason for failure on login. */
    private String failureReason;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public boolean isSucess() {
        return sucess;
    }

    public void setSucess(boolean sucess) {
        this.sucess = sucess;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
