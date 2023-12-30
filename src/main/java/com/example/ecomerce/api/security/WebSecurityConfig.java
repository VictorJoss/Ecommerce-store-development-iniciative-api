package com.example.ecomerce.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

//Establecemos las configuraciones de seguridad de nuestra aplicacion.
@Configuration
public class WebSecurityConfig {

    private JWTRequestFilter jwtRequestFilter;

    public WebSecurityConfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    //Desactiva la proteccion csrf y cors, ademas de agregar el filtro que valida el JWT
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable());

        //Agrega el filtro que valida el JWT
        http
                .addFilterBefore(jwtRequestFilter, AuthorizationFilter.class);

        //Configura las rutas que requieren autenticacion
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/api/product", "/api/auth/register", "/api/auth/login",
                                "/api/auth/verify", "/api/auth/forgot", "/api/auth/reset","/error",
                                "/websocket", "/websocket/**").permitAll()
                        .anyRequest().authenticated()
                );

        /* (en el video se usa esta forma, pero no funciona porque corresponde a una version anterior de spring y sprign security)
        http.csrf().disable().cors().disable();
        http.authorizeHttpRequests().anyRequest().permitAll();
         */

        return http.build();
    }
}
