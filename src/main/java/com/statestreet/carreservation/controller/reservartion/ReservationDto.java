package com.statestreet.carreservation.controller.reservartion;

import com.statestreet.carreservation.controller.car.CarDto;
import com.statestreet.carreservation.controller.user.UserDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class ReservationDto {
    private int id;
    private CarDto car;
    private UserDto user;
}