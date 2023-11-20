package com.example.ecomerce.service;

import com.example.ecomerce.api.model.LoginBody;
import com.example.ecomerce.api.model.RegistrationBody;
import com.example.ecomerce.exception.EmailFailureException;
import com.example.ecomerce.exception.UserAlreadyExistsException;
import com.example.ecomerce.exception.UserNotVerifiedException;
import com.example.ecomerce.model.LocalUser;
import com.example.ecomerce.model.VerificationToken;
import com.example.ecomerce.model.dao.LocalUserDao;
import com.example.ecomerce.model.dao.VerificationTokenDAO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

//Maneja la logica de negocio de los usuarios
@Service
public class UserService {

    //Maneja la tabla de usuarios
    private LocalUserDao localUserDao;
    //Encripta y verifica contraseñas
    private EncryptionService encryptionService;
    private JWTService jwtService;
    private EmailService emailService;
    private VerificationTokenDAO verificatonTokenDAO;

    //Constructor
    public UserService(LocalUserDao localUserDao, EncryptionService encryptionService, JWTService jwtService, EmailService emailService, VerificationTokenDAO verificatonTokenDAO) {
        this.localUserDao = localUserDao;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.verificatonTokenDAO = verificatonTokenDAO;
    }

    //Registra un usuario
    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException {

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
        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerificationEmail(verificationToken);
        //verificatonTokenDAO.save(verificationToken);

        return localUserDao.save(user);
    }

    //Crea un token de verificacion de email
    private VerificationToken createVerificationToken(LocalUser user){
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(user));
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);
        user.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

    //Loguea un usuario y retorna el JWT
    public String loginUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
        Optional<LocalUser> opUser = localUserDao.findByUsernameIgnoreCase(loginBody.getUsername());
        if(opUser.isPresent()){
            LocalUser user = opUser.get();
            if(encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())){
                if(user.isEmailVerified()){
                    return jwtService.generateJWT(user);
                }else{
                    List<VerificationToken> verificationTokens = user.getVerificationTokens();
                    boolean resend = verificationTokens.size() == 0 ||
                            verificationTokens.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - 60 * 60 * 1000));
                    if(resend){
                        VerificationToken verificationToken = createVerificationToken(user);
                        verificatonTokenDAO.save(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                    }
                    throw new UserNotVerifiedException(resend);
                }
            }
        }
        return null;
    }

    //Se debe agregar la anotacion @Transactional porque se modifica la base de datos.
    @Transactional
    public boolean verifyUser(String token){
        Optional<VerificationToken> opToken = verificatonTokenDAO.findByToken(token);
        if(opToken.isPresent()){
            VerificationToken verificationToken = opToken.get();
            LocalUser user = verificationToken.getUser();
            if(!user.isEmailVerified()){
                user.setEmailVerified(true);
                localUserDao.save(user);
                verificatonTokenDAO.deleteByUser(user);
                return true;
            }
        }
        return false;
    }
}
