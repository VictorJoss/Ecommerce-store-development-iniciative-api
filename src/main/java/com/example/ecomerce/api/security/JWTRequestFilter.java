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

/**
 * Filter for decoding a JWT in the Authorization header and loading the user
 * object into the authentication context.
 */
@Component
public class JWTRequestFilter extends OncePerRequestFilter implements ChannelInterceptor {

    /** The JWT Service. */
    private JWTService jwtService;
    /** The Local User DAO. */
    private LocalUserDao localUserDao;

    /**
     * Constructor for spring injection.
     * @param jwtService
     * @param localUserDao
     */
    public JWTRequestFilter(JWTService jwtService, LocalUserDao localUserDao) {
        this.jwtService = jwtService;
        this.localUserDao = localUserDao;
    }

    /**
     * Method to intercept the request and check for a JWT in the Authorization header.
     * @param request The request.
     * @param response The response.
     * @param filterChain The filter chain.
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization");
        UsernamePasswordAuthenticationToken token = checkToken(tokenHeader);
        if(token != null){
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Method to authenticate a token and return the Authentication object
     * written to the spring security context.
     * @param token The token to test.
     * @return The Authentication object if set.
     */
    private UsernamePasswordAuthenticationToken checkToken(String token){
        if(token != null && token.startsWith("Bearer ")){
            token = token.substring(7);
            try {
                String username = jwtService.getUsername(token);
                Optional<LocalUser> opUser = localUserDao.findByUsernameIgnoreCase(username);
                if(opUser.isPresent()){
                    LocalUser user = opUser.get();
                    if(user.isEmailVerified()) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, new ArrayList());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        return authentication;
                    }
                }
            }catch (JWTDecodeException ex){
            }
        }
        SecurityContextHolder.getContext().setAuthentication(null);
        return null;
    }

    /**
     * Method to intercept the websocket message and check for a JWT in the Authorization header.
     * @param message The message.
     * @param channel The channel.
     * @return The message.
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        SimpMessageType messageType = (SimpMessageType) message.getHeaders().get("simpMessageType");
        if(messageType.equals(SimpMessageType.SUBSCRIBE) || messageType.equals(SimpMessageType.MESSAGE)){
            Map nativeHeaders = (Map) message.getHeaders().get("nativeHeaders");
            if(nativeHeaders != null){
                List authTokenList = (List) nativeHeaders.get("Authorization");
                if(authTokenList != null){
                    String tokenHeader = (String) authTokenList.get(0);
                    checkToken(tokenHeader);
                }
            }
        }
        return message;
    }

}
