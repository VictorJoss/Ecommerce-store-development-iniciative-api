package com.example.ecomerce.service;

import com.example.ecomerce.api.model.LoginBody;
import com.example.ecomerce.api.model.PasswordResetBody;
import com.example.ecomerce.api.model.RegistrationBody;
import com.example.ecomerce.exception.EmailFailureException;
import com.example.ecomerce.exception.EmailNotFoundException;
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

/**
 * Service for handling user actions.
 */
@Service
public class UserService {

    /** The LocalUserDAO. */
    private LocalUserDao localUserDao;
    /** The VerificationTokenDAO. */
    private EncryptionService encryptionService;
    /** The encryption service. */
    private JWTService jwtService;
    /** The JWT service. */
    private EmailService emailService;
    /** The email service. */
    private VerificationTokenDAO verificatonTokenDAO;

    /**
     * Constructor injected by spring.
     *
     * @param localUserDao
     * @param verificationTokenDAO
     * @param encryptionService
     * @param jwtService
     * @param emailService
     */
    public UserService(LocalUserDao localUserDao, EncryptionService encryptionService, JWTService jwtService, EmailService emailService, VerificationTokenDAO verificationTokenDAO) {
        this.localUserDao = localUserDao;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.verificatonTokenDAO = verificationTokenDAO;
    }

    /**
     * Attempts to register a user given the information provided.
     * @param registrationBody The registration information.
     * @return The local user that has been written to the database.
     * @throws UserAlreadyExistsException Thrown if there is already a user with the given information.
     */
    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException {
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
        return localUserDao.save(user);
    }

    /**
     * Creates a VerificationToken object for sending to the user.
     * @param user The user the token is being generated for.
     * @return The object created.
     */
    private VerificationToken createVerificationToken(LocalUser user){
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(user));
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);
        user.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

    /**
     * Logins in a user and provides an authentication token back.
     * @param loginBody The login request.
     * @return The authentication token. Null if the request was invalid.
     */
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

    /**
     * Verifies a user from the given token.
     * @param token The token to use to verify a user.
     * @return True if it was verified, false if already verified or token invalid.
     */
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

    /**
     * Sends the user a forgot password reset based on the email provided.
     * @param email The email to send to.
     * @throws EmailNotFoundException Thrown if there is no user with that email.
     * @throws EmailFailureException
     */
    public void forgotPassword(String email) throws EmailNotFoundException, EmailFailureException {
        Optional<LocalUser> opUser = localUserDao.findByEmailIgnoreCase(email);
        if(opUser.isPresent()){
            LocalUser user = opUser.get();
            String token = jwtService.generateResetPasswordJWT(user);
            emailService.sendResetPasswordEmail(user, token);
        }else{
            throw new EmailNotFoundException();
        }
    }

    /**
     * Resets the users password using a given token and email.
     * @param body The password reset information.
     */
    public void resetPassword(PasswordResetBody body){
        String email = jwtService.getResetPasswordEmail(body.getToken());
        Optional<LocalUser> opUser = localUserDao.findByEmailIgnoreCase(email);
        if(opUser.isPresent()){
            LocalUser user = opUser.get();
            user.setPassword(encryptionService.encryptPassword(body.getPassword()));
            localUserDao.save(user);
        }
    }

    /**
     * Method to check if an authenticated user has permission to a user ID.
     * @param user The authenticated user.
     * @param id The user ID.
     * @return True if they have permission, false otherwise.
     */
    public boolean userHasPermissionToUser(LocalUser user, Long id){
        return user.getId().equals(id);
    }


}
