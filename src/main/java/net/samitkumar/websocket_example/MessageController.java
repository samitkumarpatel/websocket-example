package net.samitkumar.websocket_example;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/messages")
    public void sendMessageToUser(UserMessage userMessage) {
        messagingTemplate.convertAndSendToUser(userMessage.userId(), "/queue/messages", userMessage.message());
    }
}
