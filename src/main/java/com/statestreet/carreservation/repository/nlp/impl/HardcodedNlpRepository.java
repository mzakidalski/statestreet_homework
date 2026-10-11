package com.statestreet.carreservation.repository.nlp.impl;

import com.statestreet.carreservation.repository.common.CarType;
import com.statestreet.carreservation.repository.nlp.NlpRepository;
import com.statestreet.carreservation.repository.nlp.NlpReservation;

import java.time.LocalDateTime;

public class HardcodedNlpRepository implements NlpRepository {
    @Override
    public NlpReservation parse(String text) {
        return new NlpReservation(CarType.SUV,
                LocalDateTime.of(2026, 10, 15, 10, 0),
                LocalDateTime.of(2026, 10, 18, 10, 0));
    }
}
