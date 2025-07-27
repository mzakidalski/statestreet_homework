package com.statestreet.carreservation.service.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(int id) {
        super("User with id " + id + " not found");
    }
}
