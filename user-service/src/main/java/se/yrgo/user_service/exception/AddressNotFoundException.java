package se.yrgo.user_service.exception;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException() {
        super("Address not found");
    }
}
