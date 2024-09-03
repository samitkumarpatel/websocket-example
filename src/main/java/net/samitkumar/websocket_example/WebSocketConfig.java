package net.samitkumar.websocket_example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.setThreadNamePrefix("WebSocketTaskScheduler-");
        taskScheduler.initialize();
        return taskScheduler;
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(8192) // default: 64 * 1024
                .setSendTimeLimit(20 * 10000) // default: 10 seconds
                .setSendBufferSizeLimit(512 * 1024); // default: 512 * 1024
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //not for production use. use a message broker like RabbitMQ or ActiveMQ (see the commented code below)
        config.enableSimpleBroker("/queue/", "/topic/").setHeartbeatValue(new long[]{10000, 10000}).setTaskScheduler(taskScheduler()); // Set heartbeat to 10 seconds
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
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setHeartbeatTime(60_000);
    }
}