package com.statestreet.carreservation.repository.nlp.impl;

import com.statestreet.carreservation.repository.nlp.NlpRepository;
import com.statestreet.carreservation.repository.nlp.NlpReservation;

public class StanfordNlpAndRegexRepository implements NlpRepository {
    @Override
    public NlpReservation parse(String text) {
        throw new UnsupportedOperationException("Stanford NLP and regex parsing is not implemented yet");
    }
}
