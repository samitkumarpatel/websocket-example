package net.samitkumar.websocket_example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@Slf4j
public class WebsocketExampleApplication {

	@Bean
	Map<String, String> users() {
		return new HashMap<>();
	}

	public static void main(String[] args) {
		SpringApplication.run(WebsocketExampleApplication.class, args);
	}

	@EventListener
	public void onSessionConnect(SessionConnectEvent event) {
		log.info("onSessionConnect {}",event);
		event.getMessage().getHeaders().forEach((key, value) -> log.info("{}={}", key, value));
		var sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
		var nativeHeaders =  event.getMessage().getHeaders().get("nativeHeaders", Map.class);

		if (nativeHeaders != null && nativeHeaders.containsKey("userId")) {
			var userId = ((List<String>) nativeHeaders.get("userId")).get(0);
			log.info("attached sessionId={} with userId={}", sessionId, userId);
			users().put(userId, sessionId);
		}
		log.info("users() {}", users());
	}

	@EventListener
	public void onSessionDisconnect(SessionDisconnectEvent event) {

	}

}
