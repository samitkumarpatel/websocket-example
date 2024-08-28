package net.samitkumar.websocket_example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@Slf4j
public class WebsocketExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsocketExampleApplication.class, args);
	}

	@Bean
	List<String> sessions() {
		return new ArrayList<>();
	}

	@EventListener
	public void onSessionConnect(SessionConnectEvent event) {
		log.info("onSessionConnect {}",event);
		event.getMessage().getHeaders().forEach((key, value) -> log.info("{}: {}", key, value));
		var sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
		sessions().add(sessionId);
	}

	@EventListener
	public void onSessionDisconnect(SessionDisconnectEvent event) {
		log.info("onSessionDisconnect {}",event);
		//event.getMessage().getHeaders().forEach((key, value) -> log.info("{}: {}", key, value));
		var sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
		sessions().remove(sessionId);
	}

}
