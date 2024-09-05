package net.samitkumar.websocket_example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SimpMessageSendingOperations messagingSendingTemplate;
    private final Map<String, String> users;

    /*@MessageMapping("/private")
    @SendToUser("/queue/private")
    public String sendMessageToUser(@Payload UserMessage userMessage, @Headers Map<Object, Object> headers) {
        log.info("sendMessageToUser userMessage: {} Headers: {}", userMessage, headers);
        messagingTemplate.convertAndSendToUser(userMessage.to(), "/queue/private", userMessage.message());
        return userMessage.message();
    }*/

    @MessageMapping("/private")
    public void sendMessageToUser(@Payload UserMessage userMessage, @Headers Map<Object, Object> headers, Principal principal) {
        var sendTo = users.get(userMessage.to());
        log.info("sendMessageToUser sendTo: {} userMessage: {} Headers: {}",sendTo, userMessage, headers);
        //messagingTemplate.convertAndSendToUser(sendTo, "/queue/private", userMessage.message());
        messagingSendingTemplate.convertAndSendToUser( sendTo, "/queue/private", userMessage.message());
    }

    @MessageMapping("/public")
    @SendTo("/topic/public")
    public String sendGreetingToUser(@Payload String message, @Headers Map<Object, Object> headers, Principal principal) throws InterruptedException {
        log.info("sendGreetingToUser Principle {} Headers: {}, {}", principal, headers, message);
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
class ApiController {
    final Map<String, String> users;

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, String>>> allUsers() {
        List<Map<String, String>> response = users.entrySet().stream().map(entry -> Map.of("name", entry.getKey())).toList();
        return ResponseEntity.ok(response);
    }
}