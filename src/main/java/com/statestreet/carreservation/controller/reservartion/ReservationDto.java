package com.statestreet.carreservation.controller.reservartion;

import com.statestreet.carreservation.controller.car.CarDto;
import com.statestreet.carreservation.controller.user.UserDto;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ReservationDto {
    private int id;
    private CarDto car;
    private UserDto user;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
