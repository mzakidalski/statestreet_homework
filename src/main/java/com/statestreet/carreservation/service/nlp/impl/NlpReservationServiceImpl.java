package com.statestreet.carreservation.service.nlp.impl;

import com.statestreet.carreservation.repository.nlp.NlpReservation;
import com.statestreet.carreservation.service.nlp.NlpReservationService;
import com.statestreet.carreservation.service.nlp.NlpReservationServiceException;

import com.statestreet.carreservation.repository.nlp.NlpRepository;
import com.statestreet.carreservation.repository.reservation.Reservation;
import com.statestreet.carreservation.service.reservation.ReservationService;

import java.util.ArrayList;
import java.util.List;

public class NlpReservationServiceImpl implements NlpReservationService {
    private final NlpRepository repository;
    private final ReservationService reservationService;

    public NlpReservationServiceImpl(NlpRepository repository, ReservationService reservationService) {
        this.repository = repository;
        this.reservationService = reservationService;
    }

    @Override
    public Reservation makeReservation(int userId, String text) {
        NlpReservation parsed = repository.parse(text);
        List<String> validationErrors = new ArrayList<>();
        if (parsed.carType() == null) {
            validationErrors.add(LACK_OF_CAR_TYPE);
        }
        if (parsed.startTime() == null) {
            validationErrors.add(LACK_OF_RESERVATION_START_DATE);
        }
        if (parsed.endTime() == null) {
            validationErrors.add(LACK_OF_RESERVATION_END_DATE);
        } else if (parsed.startTime() != null && !parsed.endTime().isAfter(parsed.startTime())) {
            validationErrors.add(INVALID_RESERVATION_PERIOD);
        }
        if (!validationErrors.isEmpty()) {
            throw new NlpReservationServiceException(String.join("; ", validationErrors));
        }

        try {
            return reservationService.makeReservation(userId, parsed.carType(), parsed.startTime(),
                    parsed.endTime());
        } catch (RuntimeException ex) {
            throw new NlpReservationServiceException(ex.getMessage(), ex);
        }
    }
}
