package se.yrgo.user_service.messaging;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    @JmsListener(destination = "user.registered")
    public void handleUserRegistered(String message) {
        System.out.println("User registered: " + message);
    }

    @JmsListener(destination = "user.updated")
    public void handleUserUpdated(String message) {
        System.out.println("User updated: " + message);
    }

    @JmsListener(destination = "user.deleted")
    public void handleUserDeleted(String message) {
        System.out.println("User deleted: " + message);
    }

    @JmsListener(destination = "address.created")
    public void handleAddressCreated(String message) {
        System.out.println("Address created: " + message);
    }

    @JmsListener(destination = "address.updated")
    public void handleAddressUpdated(String message) {
        System.out.println("Address updated: " + message);
    }

    @JmsListener(destination = "address.deleted")
    public void handleAddressDeleted(String message) {
        System.out.println("Address deleted: " + message);
    }
}
