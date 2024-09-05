package net.samitkumar.websocket_example;

import com.sun.security.auth.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //not for production use. use a message broker like RabbitMQ or ActiveMQ (see the commented code below)
        config.enableSimpleBroker("/queue/", "/topic/");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    /*@Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost("rabbitmq.example.com")
                .setRelayPort(61613)
                .setClientLogin("guest")
                .setClientPasscode("guest");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");  // User-specific messaging prefix
    }*/

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register a STOMP endpoint at '/stomp-endpoint'
        registry.addEndpoint("/stomp-endpoint")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        log.info("##### request.headers : {}", request.getHeaders());
                        var uuid = UUID.randomUUID().toString();
                        attributes.put("UUID", uuid);
                        return new UserPrincipal(uuid);
                    }
                })
                .setAllowedOriginPatterns("*")
                .withSockJS().setHeartbeatTime(60_000);
    }
}