package com.statestreet.carreservation.controller.car;

import com.statestreet.carreservation.repository.car.Car;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CarMapper {

    public CarDto toDto(Car car) {
        if (car == null) {
            return null;
        }
        return new CarDto(
                car.getId(),
                car.getType(),
                car.getModel(),
                car.getMake()
        );
    }

    public Car toEntity(CarDto dto) {
        if (dto == null) {
            return null;
        }
        Car car = new Car();
        car.setId(dto.getId());
        car.setType(dto.getType());
        car.setModel(dto.getModel());
        car.setMake(dto.getMake());
        return car;
    }

    public List<CarDto> toDtoList(List<Car> cars) {
        return cars == null ? null : cars.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Car> toEntityList(List<CarDto> dtos) {
        return dtos == null ? null : dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
