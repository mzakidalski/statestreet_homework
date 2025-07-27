package com.statestreet.carreservation.service.car;

import com.statestreet.carreservation.repository.common.CarType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CarDto {
    private int id;
    private CarType type;
    private String model;
    private String make;
}

