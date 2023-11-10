package com.example.ecomerce.api.controller.auth;

import com.example.ecomerce.api.model.LoginBody;
import com.example.ecomerce.api.model.LoginResponse;
import com.example.ecomerce.api.model.RegistrationBody;
import com.example.ecomerce.exception.UserAlreadyExistsException;
import com.example.ecomerce.model.LocalUser;
import com.example.ecomerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

//Maneja las peticiones de autenticacion
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    //Maneja la logica de negocio de los usuarios
    private UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    //Registra un usuario
    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody) {
        try{
            userService.registerUser(registrationBody);
            //Retorna un codigo 200
            return ResponseEntity.ok().build();
        }catch (UserAlreadyExistsException e){
            //Retorna un codigo 409
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }

    //Loguea un usuario con un JWT
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody){
        String jwt = userService.loginUser(loginBody);
        if(jwt == null){
            //Retorna un codigo 400
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }else {
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            //Retorna un codigo 200 y el JWT
            return ResponseEntity.ok(response);
        }
    }

    //Retorna el usuario logueado actualmente con un JWT en el header de la peticion
    @GetMapping("/me")
    public LocalUser getLoggedUserProfile(@AuthenticationPrincipal LocalUser user){
        return user;
    }




}
