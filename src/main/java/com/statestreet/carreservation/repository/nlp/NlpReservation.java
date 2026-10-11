package com.statestreet.carreservation.repository.nlp;

import com.statestreet.carreservation.repository.common.CarType;

import java.time.LocalDateTime;

public record NlpReservation(CarType carType,
                             LocalDateTime startTime,
                             LocalDateTime endTime) { }
