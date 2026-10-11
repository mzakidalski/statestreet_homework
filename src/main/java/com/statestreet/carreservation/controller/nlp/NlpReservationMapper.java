package com.statestreet.carreservation.controller.nlp;

import com.statestreet.carreservation.controller.car.CarMapper;
import com.statestreet.carreservation.controller.user.UserMapper;
import com.statestreet.carreservation.repository.reservation.Reservation;
import org.springframework.stereotype.Component;

@Component
public class NlpReservationMapper {
    private final CarMapper carMapper;
    private final UserMapper userMapper;

    public NlpReservationMapper(CarMapper carMapper, UserMapper userMapper) {
        this.carMapper = carMapper;
        this.userMapper = userMapper;
    }

    public NlpReservationDto toDto(Reservation reservation) {
        return new NlpReservationDto(reservation.getId(), carMapper.toDto(reservation.getCar()),
                userMapper.toDto(reservation.getUser()));
    }
}
