package se.yrgo.user_service.messaging;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    @JmsListener(destination = "user.registered")
    public void receiveMessage(String message) {
        System.out.println("Mottaget meddelande: " + message);
    }
}