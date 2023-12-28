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

@Service
public class EmailService {

    //Cuidado con esto, es peligroso no colocarlo bien.
    @Value("${email.from}")
    private String fromAddress;
    @Value("${app.frontend.url}")
    private String frontendUrl;

    private JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    private SimpleMailMessage makeMailMessage(){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromAddress);
        return simpleMailMessage;
    }

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
