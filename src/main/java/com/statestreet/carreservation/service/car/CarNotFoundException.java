package com.statestreet.carreservation.service.car;

public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException(int id) {
        super("Car with id " + id + " was not found.");
    }
}
