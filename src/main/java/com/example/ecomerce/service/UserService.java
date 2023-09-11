package com.example.ecomerce.service;

import com.example.ecomerce.api.model.RegistrationBody;
import com.example.ecomerce.exception.UserAlreadyExistsException;
import com.example.ecomerce.model.LocalUser;
import com.example.ecomerce.model.dao.LocalUserDao;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private LocalUserDao localUserDao;

    public UserService(LocalUserDao localUserDao) {
        this.localUserDao = localUserDao;
    }

    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException {

        if (localUserDao.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
        || localUserDao.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()){
            throw new UserAlreadyExistsException();
        }
        LocalUser user = new LocalUser();
        user.setUsername(registrationBody.getUsername());
        user.setEmail(registrationBody.getEmail());
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());
        //TODO: Encrypt password!!
        user.setPassword(registrationBody.getPassword());

        return localUserDao.save(user);
    }
}
