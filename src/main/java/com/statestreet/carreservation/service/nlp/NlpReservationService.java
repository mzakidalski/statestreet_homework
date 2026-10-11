package com.statestreet.carreservation.service.nlp;

import com.statestreet.carreservation.repository.reservation.Reservation;

public interface NlpReservationService {
    String LACK_OF_CAR_TYPE = "Lack of car Type";
    String LACK_OF_RESERVATION_START_DATE = "Lack of reservation start date";
    String LACK_OF_RESERVATION_END_DATE = "Lack of reservation end date";
    String INVALID_RESERVATION_PERIOD = "Reservation end date must be after start date";

    Reservation makeReservation(int userId, String text);
}
