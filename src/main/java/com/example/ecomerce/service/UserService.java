package com.example.ecomerce.service;

import com.example.ecomerce.api.model.LoginBody;
import com.example.ecomerce.api.model.RegistrationBody;
import com.example.ecomerce.exception.UserAlreadyExistsException;
import com.example.ecomerce.model.LocalUser;
import com.example.ecomerce.model.dao.LocalUserDao;
import org.springframework.stereotype.Service;

import java.util.Optional;

//Maneja la logica de negocio de los usuarios
@Service
public class UserService {

    //Maneja la tabla de usuarios
    private LocalUserDao localUserDao;
    //Encripta y verifica contraseñas
    private EncryptionService encryptionService;
    private JWTService jwtService;

    //Constructor
    public UserService(LocalUserDao localUserDao, EncryptionService encryptionService, JWTService jwtService) {
        this.localUserDao = localUserDao;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
    }

    //Registra un usuario
    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException {

        //Verifica si el username o el email ya existen
        if (localUserDao.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
        || localUserDao.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()){
            throw new UserAlreadyExistsException();
        }
        LocalUser user = new LocalUser();
        user.setUsername(registrationBody.getUsername());
        user.setEmail(registrationBody.getEmail());
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));

        return localUserDao.save(user);
    }

    //Loguea un usuario y retorna el JWT
    public String loginUser(LoginBody loginBody){
        Optional<LocalUser> opUser = localUserDao.findByUsernameIgnoreCase(loginBody.getUsername());
        if(opUser.isPresent()){
            LocalUser user = opUser.get();
            if(encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())){
                return jwtService.generateJWT(user);
            }
        }
        return null;
    }

}
