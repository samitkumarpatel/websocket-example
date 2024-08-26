package net.samitkumar.websocket_example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/messages")
    public void sendMessageToUser(UserMessage userMessage) {
        log.info("{}", userMessage);
        messagingTemplate.convertAndSendToUser(userMessage.userId(), "/queue/messages", userMessage.message());
    }
}
