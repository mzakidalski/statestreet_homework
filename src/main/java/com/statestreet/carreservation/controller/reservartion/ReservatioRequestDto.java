package com.statestreet.carreservation.controller.reservartion;

import com.statestreet.carreservation.repository.common.CarType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class ReservatioRequestDto {
    int userId;
    CarType carType;
    LocalDateTime startTime;
    int numOfDays;
}
