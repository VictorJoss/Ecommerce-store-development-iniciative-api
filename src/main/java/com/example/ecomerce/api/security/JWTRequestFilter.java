package com.example.ecomerce.api.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.example.ecomerce.model.LocalUser;
import com.example.ecomerce.model.dao.LocalUserDao;
import com.example.ecomerce.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//Maneja las peticiones de autenticacion con JWT y las agrega al contexto de seguridad de Spring Security
@Component
public class JWTRequestFilter extends OncePerRequestFilter implements ChannelInterceptor {

    private JWTService jwtService;
    private LocalUserDao localUserDao;

    public JWTRequestFilter(JWTService jwtService, LocalUserDao localUserDao) {
        this.jwtService = jwtService;
        this.localUserDao = localUserDao;
    }

    //Filtra las peticiones
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Obtiene el token del header
        String tokenHeader = request.getHeader("Authorization");
        //Verifica el token
        UsernamePasswordAuthenticationToken token = checkToken(tokenHeader);
        //Si el token no es nulo se agrega al contexto de seguridad de Spring Security
        if(token != null){
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        }
        //Continua con la peticion normalmente sin importar si se agrego o no el token de autenticacion
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken checkToken(String token){
        //Si el token no es nulo y empieza con "Bearer "
        if(token != null && token.startsWith("Bearer ")){
            //Obtiene el token
            token = token.substring(7);
            try {
                //Obtiene el username del token
                String username = jwtService.getUsername(token);
                //Busca el usuario en la base de datos
                Optional<LocalUser> opUser = localUserDao.findByUsernameIgnoreCase(username);
                //Si el usuario existe
                if(opUser.isPresent()){
                    LocalUser user = opUser.get();
                    if(user.isEmailVerified()) {
                        //Crea un token de autenticacion y lo agrega al contexto de seguridad de Spring Security para que pueda ser usado por los controladores
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, new ArrayList());
                        //Se agrega al contexto de seguridad
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        return authentication;
                    }
                }
                //Si el usuario no existe no hace nada
            }catch (JWTDecodeException ex){
            }
        }
        //Si el token es nulo o no empieza con "Bearer " o el usuario no existe
        // se agrega un token nulo al contexto de seguridad de Spring Security
        SecurityContextHolder.getContext().setAuthentication(null);
        return null;
    }

    //Este metodo se ejecuta antes de enviar un mensaje por un canal y verifica que el usuario este autenticado
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        //Obtiene el tipo de mensaje
        SimpMessageType messageType = (SimpMessageType) message.getHeaders().get("simpMessageType");
        //se verifica que el mensaje sea de tipo SUBSCRIBE o MESSAGE
        //Se obtiene el header "nativeHeaders" que contiene los headers de la peticion
        if(messageType.equals(SimpMessageType.SUBSCRIBE) || messageType.equals(SimpMessageType.MESSAGE)){
            Map nativeHeaders = (Map) message.getHeaders().get("nativeHeaders");
            //Se verifica que el header nativeHeaders no sea nulo y se obtiene el header "Authorization"
            if(nativeHeaders != null){
                List authTokenList = (List) nativeHeaders.get("Authorization");
                //Se verifica que el header Authorization no sea nulo
                if(authTokenList != null){
                    //Se obtiene el primer valor del header "Authorization"
                    String tokenHeader = (String) authTokenList.get(0);
                    //Se verifica el token
                    checkToken(tokenHeader);
                }
            }
        }
        //Se retorna el mensaje
        return message;
    }



}
