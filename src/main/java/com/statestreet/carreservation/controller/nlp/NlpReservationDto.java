package com.statestreet.carreservation.controller.nlp;

import com.statestreet.carreservation.controller.car.CarDto;
import com.statestreet.carreservation.controller.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class NlpReservationDto {
    private int id;
    private CarDto car;
    private UserDto user;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
