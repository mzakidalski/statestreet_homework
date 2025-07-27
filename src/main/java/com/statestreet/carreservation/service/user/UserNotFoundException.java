package com.statestreet.carreservation.service.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(int id) {
        super("User with id " + id + " was not found.");
    }
}
