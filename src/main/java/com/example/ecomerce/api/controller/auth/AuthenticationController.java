package com.example.ecomerce.api.controller.auth;

import com.example.ecomerce.api.model.LoginBody;
import com.example.ecomerce.api.model.LoginResponse;
import com.example.ecomerce.api.model.PasswordResetBody;
import com.example.ecomerce.api.model.RegistrationBody;
import com.example.ecomerce.exception.EmailFailureException;
import com.example.ecomerce.exception.EmailNotFoundException;
import com.example.ecomerce.exception.UserAlreadyExistsException;
import com.example.ecomerce.exception.UserNotVerifiedException;
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
        }catch (UserAlreadyExistsException ex){
            //Retorna un codigo 409
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
            //En caso de que falle el envio del email
        } catch (EmailFailureException ex) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    //Loguea un usuario con un JWT
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody){
        String jwt = null;
        try {
            jwt = userService.loginUser(loginBody);
        } catch (UserNotVerifiedException ex) {
            LoginResponse response = new LoginResponse();
            response.setSucess(false);
            String reason = "USER_NOT_VERIFIED";
            if(ex.isNewEmailSent()){
                reason += "_NEW_EMAIL_SENT";
            }
            response.setFailureReason(reason);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            //En caso de que falle el envio del email
        } catch (EmailFailureException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if(jwt == null){
            //Retorna un codigo 400
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }else {
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            response.setSucess(true);
            //Retorna un codigo 200 y el JWT
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity verifyEmail(@RequestParam String token){
        if(userService.verifyUser(token)){
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    //Retorna el usuario logueado actualmente con un JWT en el header de la peticion
    @GetMapping("/me")
    public LocalUser getLoggedUserProfile(@AuthenticationPrincipal LocalUser user){
        return user;
    }

    @PostMapping("/forgot")
    public ResponseEntity forgotPassword(@RequestParam String email){
        try{
            userService.forgotPassword(email);
            return ResponseEntity.ok().build();
        }catch (EmailNotFoundException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch (EmailFailureException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/reset")
    public ResponseEntity resetPassword(@Valid @RequestBody PasswordResetBody body){
        userService.resetPassword(body);
        return ResponseEntity.ok().build();
    }
}
