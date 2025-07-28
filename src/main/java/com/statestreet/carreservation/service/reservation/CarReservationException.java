package com.statestreet.carreservation.service.reservation;

import com.statestreet.carreservation.repository.common.CarType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CarReservationException extends RuntimeException {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CarReservationException(LocalDateTime startDate, LocalDateTime endDate, CarType type) {
        super("No car of type " + type + " available between dates: " +
                startDate.format(FORMATTER) + " and " + endDate.format(FORMATTER) + ".");
    }

}
