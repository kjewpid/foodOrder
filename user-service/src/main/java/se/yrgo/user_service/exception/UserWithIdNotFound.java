package se.yrgo.user_service.exception;

public class UserWithIdNotFound extends RuntimeException {
    public UserWithIdNotFound(Long id) {
        super("User with id " + id + " not found");
    }
}
