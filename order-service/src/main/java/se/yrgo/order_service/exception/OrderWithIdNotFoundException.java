package se.yrgo.order_service.exception;

public class OrderWithIdNotFoundException extends RuntimeException {
    public OrderWithIdNotFoundException(Long id) {
        super("Order with id " + id + " not found");
    }
}
