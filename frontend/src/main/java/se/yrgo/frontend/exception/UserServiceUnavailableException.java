package se.yrgo.frontend.exception;

public class UserServiceUnavailableException extends RuntimeException {

    public UserServiceUnavailableException(Throwable cause) {
        super("Användartjänsten är tillfälligt otillgänglig.", cause);
    }
}