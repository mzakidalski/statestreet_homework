package com.statestreet.carreservation.repository.nlp.impl;

import com.statestreet.carreservation.repository.nlp.NlpRepository;
import com.statestreet.carreservation.repository.nlp.NlpReservation;

public class RegexNlpRepository implements NlpRepository {
    @Override
    public NlpReservation parse(String text) {
        throw new UnsupportedOperationException("Regex NLP parsing is not implemented yet");
    }
}
