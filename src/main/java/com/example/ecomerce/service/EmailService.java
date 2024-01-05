package com.example.ecomerce.service;

import com.example.ecomerce.exception.EmailFailureException;
import com.example.ecomerce.model.LocalUser;
import com.example.ecomerce.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for handling emails being sent.
 */
@Service
public class EmailService {

    /** The from address to use on emails. */
    @Value("${email.from}")
    private String fromAddress;
    /** The url of the front end for links. */
    @Value("${app.frontend.url}")
    private String frontendUrl;
    /** The JavaMailSender instance. */
    private JavaMailSender javaMailSender;

    /**
     * Constructor for spring injection.
     * @param javaMailSender
     */
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Makes a SimpleMailMessage for sending.
     * @return The SimpleMailMessage created.
     */
    private SimpleMailMessage makeMailMessage(){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromAddress);
        return simpleMailMessage;
    }

    /**
     * Sends a verification email to the user.
     * @param verificationToken The verification token to be sent.
     * @throws EmailFailureException Thrown if are unable to send the email.
     */
    public void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException {
        SimpleMailMessage message = makeMailMessage();
        message.setTo(verificationToken.getUser().getEmail());
        message.setSubject("Verifica tu email para activar tu cuenta");
        message.setText("Por favor haz click en el siguiente link para verificar tu email: \n"+
                frontendUrl + "api/auth/verify?token=" + verificationToken.getToken());
        try{
            javaMailSender.send(message);
        }catch (MailException ex){
            throw new EmailFailureException();
        }
    }

    /**
     * Sends a password reset request email to the user.
     * @param user The user to send to.
     * @param token The token to send the user for reset.
     * @throws EmailFailureException
     */
    public void sendResetPasswordEmail(LocalUser user, String token) throws EmailFailureException {
        SimpleMailMessage message = makeMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Tu solicitud de cambio contraseña");
        message.setText("Solicitaste restablecer tu contrseña en nuestra pagina web. Por favor"+
                "haz click en el siguiente link para cambiar tu contraseña: \n"+
                frontendUrl + "/api/auth/reset?token=" + token);
        try{
            javaMailSender.send(message);
        }catch (MailException ex){
            throw new EmailFailureException();
        }
    }

}
