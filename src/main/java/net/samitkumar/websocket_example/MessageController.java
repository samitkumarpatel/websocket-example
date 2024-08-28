package net.samitkumar.websocket_example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;

    /*@MessageMapping("/private")
    @SendToUser("/queue/private")
    public UserMessage sendMessageToUser(UserMessage userMessage, Principal principal, @Headers Map<Object, Object> headers) {
        log.info("sendMessageToUser Headers: {}", headers);
        return userMessage;
    }*/

    @MessageMapping("/private")
    public void sendMessageToUser(@Payload UserMessage message, @Headers Map<Object, Object> headers) {
        log.info("messagingTemplate::sendMessageToUser.Headers: {}", headers);
        log.info("messagingTemplate::sendMessageToUser.Message: {}", message);
        messagingTemplate.convertAndSendToUser(message.to(), "/queue/private", message.message());
    }

    @MessageMapping("/public")
    @SendTo("/topic/public")
    public String sendGreeting(String message, @Headers Map<Object, Object> headers) throws InterruptedException {
        log.info("sendGreeting.Headers: {}", headers);
        Thread.sleep(1000);
        return message;
    }

    @MessageExceptionHandler
    @SendToUser(destinations="/queue/errors")
    public String handleException(Exception exception) {
        return exception.getMessage();
    }
}

@RestController
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*")
class MessageRestController {
    private final List<String> sessions;

    @GetMapping("/sessions")
    public List<String> sessions() {
        return sessions;
    }
}