package net.samitkumar.websocket_example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/private")
    @SendToUser("/queue/private")
    public UserMessage sendMessageToUser(UserMessage userMessage, @Headers Map<Object, Object> headers, Principal principal) {
        log.info("sendMessageToUser Principle {}, userMessage: {} Headers: {}", principal, userMessage, headers);
        return userMessage;
    }

    @MessageMapping("/public")
    @SendTo("/topic/public")
    public String sendGreetingToUser(String message, @Headers Map<Object, Object> headers, Principal principal) throws InterruptedException {
        log.info("sendGreetingToUser Principle {} Headers: {}, {}", principal, headers, message);
        Thread.sleep(1000);
        return message;
    }

    @MessageExceptionHandler
    @SendToUser(destinations="/queue/errors", broadcast=false)
    public String handleException(Exception exception) {
        return exception.getMessage();
    }
}
