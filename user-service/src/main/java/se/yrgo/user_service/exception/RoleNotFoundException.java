package se.yrgo.user_service.exception;

public class RoleNotFound extends RuntimeException {
    public RoleNotFound(String message) {
        super(message);
    }
}
