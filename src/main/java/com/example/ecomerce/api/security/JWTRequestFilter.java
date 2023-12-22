package com.example.ecomerce.api.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.example.ecomerce.model.LocalUser;
import com.example.ecomerce.model.dao.LocalUserDao;
import com.example.ecomerce.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

//Maneja las peticiones de autenticacion con JWT y las agrega al contexto de seguridad de Spring Security
@Component
public class JWTRequestFilter extends OncePerRequestFilter {

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
        //Si el token no es nulo y empieza con "Bearer "
        if(tokenHeader != null && tokenHeader.startsWith("Bearer ")){
            //Obtiene el token
            String token = tokenHeader.substring(7);
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
                        //Se le agrega la informacion de la peticion
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        //Se agrega al contexto de seguridad
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
                //Si el usuario no existe no hace nada
            }catch (JWTDecodeException ex){
            }
        }
        //Continua con la peticion normalmente sin importar si se agrego o no el token de autenticacion
        filterChain.doFilter(request, response);
    }
}
