package com.example.ecomerce.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

/**
 * Configuration of the security on endpoints.
 */
@Configuration
public class WebSecurityConfig {

    private JWTRequestFilter jwtRequestFilter;

    /**
     * Constructor for spring injection.
     * @param jwtRequestFilter
     */
    public WebSecurityConfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * Filter chain to configure security.
     * @param http The security object.
     * @return The chain built.
     * @throws Exception Thrown on error configuring.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable());
        // We need to make sure our authentication filter is run before the http request filter is run.
        http
                .addFilterBefore(jwtRequestFilter, AuthorizationFilter.class);
        // Specific exclusions or rules.
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/api/product", "/api/auth/register", "/api/auth/login",
                                "/api/auth/verify", "/api/auth/forgot", "/api/auth/reset","/error",
                                "/websocket", "/websocket/**").permitAll()
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        // Everything else should be authenticated.
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    /** The list of endpoints to allow access to. */
    private static final String[] AUTH_WHITELIST = {
            "/api/v1/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs.yaml",
            "/v3/api-docs/**",
            "/swagger-ui.html"
    };
}
