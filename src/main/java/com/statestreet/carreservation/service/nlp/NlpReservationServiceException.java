package com.statestreet.carreservation.service.nlp;

public class NlpReservationServiceException extends RuntimeException {
    public NlpReservationServiceException(String message) {
        super(message);
    }

    public NlpReservationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
