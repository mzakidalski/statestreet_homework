package com.statestreet.carreservation.controller.nlp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NlpReservationRequestDto {
    private int userId;
    private String text;
}
