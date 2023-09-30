package com.example.ecomerce.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

//Establecemos las configuraciones de seguridad de nuestra aplicacion.
@Configuration
public class WebSecurityConfig {

    //Desactiva la proteccion csrf y cors, ademas de permitir todas las peticiones para cualquier ruta.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable());
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().permitAll()
                );

        /* (en el video se usa esta forma, pero no funciona porque corresponde a una version anterior de spring y sprign security)
        http.csrf().disable().cors().disable();
        http.authorizeHttpRequests().anyRequest().permitAll();
         */

        return http.build();
    }
}
