package se.yrgo.user_service.exception;

public class UserWithIdNotFoundException extends RuntimeException {
    public UserWithIdNotFoundException(Long id) {
        super("User with id " + id + " not found");
    }
}
