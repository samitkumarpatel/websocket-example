package net.samitkumar.websocket_example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class WebsocketExampleApplication {

	final SimpMessagingTemplate simpMessagingTemplate;

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

		event.getMessage().getHeaders().forEach((key, value) -> log.info("####onSessionConnect {}={}", key, value));
		var uuid = Objects.requireNonNull(event.getUser()).getName();
		var sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
		var nativeHeaders =  event.getMessage().getHeaders().get("nativeHeaders", Map.class);

		if (nativeHeaders != null && nativeHeaders.containsKey("userId")) {
			var userId = ((List<String>) nativeHeaders.get("userId")).getFirst();
			log.info("attached sessionId={} with userId={}", sessionId, userId);
			users().put(userId, uuid);
		}

		log.info("############### refresh users() {} ###########", users());
		simpMessagingTemplate.convertAndSend("/topic/refresh-user", Map.of("users",users()));
	}

	@EventListener
	public void onSessionDisconnect(SessionDisconnectEvent event) {
		var uuid = Objects.requireNonNull(event.getUser()).getName();
		//remove this user from the users()

		//find key from value
		var keyToRemove = users().keySet().stream().filter(k -> users().get(k).equals(uuid)).findFirst().get();
		users().remove(keyToRemove);

		log.info("############### clean-up users() {} ############", users());
		simpMessagingTemplate.convertAndSend("/topic/refresh-user", Map.of("users",users()));
	}

}
