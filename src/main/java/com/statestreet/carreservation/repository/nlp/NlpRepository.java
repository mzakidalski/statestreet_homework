package com.statestreet.carreservation.repository.nlp;

public interface NlpRepository {
    NlpReservation parse(String text);
}
