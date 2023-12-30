package com.example.ecomerce.api.security;

import com.example.ecomerce.model.LocalUser;
import com.example.ecomerce.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Map;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private ApplicationContext context;
    private JWTRequestFilter jwtRequestFilter;
    private UserService userService;
    //Se crea un AntPathMatcher para comparar rutas
    private static  final AntPathMatcher MATCHER = new AntPathMatcher();

    public WebSocketConfiguration(ApplicationContext context, JWTRequestFilter jwtRequestFilter, UserService userService) {
        this.context = context;
        this.jwtRequestFilter = jwtRequestFilter;
        this.userService = userService;
    }

    //Se configura el punto de entrada para el websocket
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").setAllowedOriginPatterns("**").withSockJS();
    }

    //Se configura el broker de mensajes.
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //Se configura el broker para que los mensajes que lleguen a /topic se envien a todos los usuarios
        registry.enableSimpleBroker("/topic");
        //Se configura el broker para que los mensajes que lleguen a /app se envien a los controladores
        registry.setApplicationDestinationPrefixes("/app");
    }

    //Este metodo se encarga de configurar el canal de entrada de mensajes
    private AuthorizationManager<Message<?>> makeMessageAuthorizationManager(){
        //Se crea un builder para configurar el AuthorizationManager
        MessageMatcherDelegatingAuthorizationManager.Builder message =
                new MessageMatcherDelegatingAuthorizationManager.Builder();
        message.
                //Indica que la ruta /topic/user/** requiere autenticacion para enviar mensajes
                simpDestMatchers("/topic/user/**").authenticated()
                .anyMessage().permitAll();
        return message.build();
    }

    //Este metodo
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        //Se crea un AuthorizationManager para los mensajes
        AuthorizationManager<Message<?>> authorizationManager =
                makeMessageAuthorizationManager();
        //Se crea un AuthorizationChannelInterceptor para los mensajes
        AuthorizationChannelInterceptor authInterceptor =
                new AuthorizationChannelInterceptor(authorizationManager);
        //Se crea un AuthorizationEventPublisher para los mensajes
        AuthorizationEventPublisher publisher =
                new SpringAuthorizationEventPublisher(context);
        //Se configura el AuthorizationEventPublisher para el AuthorizationChannelInterceptor
        authInterceptor.setAuthorizationEventPublisher(publisher);
        //Se configura el AuthorizationChannelInterceptor y el JWTRequestFilter para el canal de entrada
        registration.interceptors(jwtRequestFilter, authInterceptor,
                new RejectClientMessagesOnChannelsChannelInterceptor(),
                new DestinationLevelAuthorizationChannelInterceptor());
    }

    private class RejectClientMessagesOnChannelsChannelInterceptor implements ChannelInterceptor{

        private  String[] paths = new String[] {"topic/user/*/address"};

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            if (message.getHeaders().get("simpMessageType").equals(SimpMessageType.MESSAGE)) {
                String destination = (String) message.getHeaders().get(
                        "simpDestination");
                for (String path: paths) {
                    if (MATCHER.match(path, destination))
                        message = null;
                }
            }
            return message;
        }
    }

    private class DestinationLevelAuthorizationChannelInterceptor implements ChannelInterceptor{

        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            if (message.getHeaders().get("simpMessageType").equals(SimpMessageType.SUBSCRIBE)) {
                String destination = (String) message.getHeaders().get(
                        "simpDestination");
                String userTopicMatcher = "/topic/user/{userId}/**";
                if (MATCHER.match(userTopicMatcher, destination)) {
                    Map<String, String> params = MATCHER.extractUriTemplateVariables(
                            userTopicMatcher, destination);
                    try {
                        Long userId = Long.valueOf(params.get("userId"));
                        Authentication authentication =
                                SecurityContextHolder.getContext().getAuthentication();
                        if (authentication != null) {
                            LocalUser user = (LocalUser) authentication.getPrincipal();
                            if (!userService.userHasPermissionToUser(user, userId)) {
                                message = null;
                            }
                        } else {
                            message = null;
                        }
                    } catch (NumberFormatException ex) {
                        message = null;
                    }
                }
            }
            return message;
        }
    }
}



