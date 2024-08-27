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

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/private")
    @SendToUser("/queue/private")
    public UserMessage sendMessageToUser(UserMessage userMessage, @Headers Map<Object, Object> headers) {
        log.info("sendMessageToUser userMessage: {} Headers: {}", userMessage, headers);
        return userMessage;
    }

//    @MessageMapping("/private")
//    public void sendMessageToUser(UserMessage message, @Headers Map<Object, Object> headers) {
//        log.info("messagingTemplate::sendMessageToUser.Headers: {}", headers);
//        messagingTemplate.convertAndSendToUser(message.to(), "/queue/private", message.message());
//    }

    @MessageMapping("/public")
    @SendTo("/topic/public")
    public String sendGreetingToUser(String message, @Headers Map<Object, Object> headers) throws InterruptedException {
        log.info("sendGreetingToUser.Headers: {}, {}", message, headers);
        Thread.sleep(1000);
        return message;
    }

    @MessageExceptionHandler
    @SendToUser(destinations="/queue/errors", broadcast=false)
    public String handleException(Exception exception) {
        return exception.getMessage();
    }
}
