package com.statestreet.carreservation.controller.reservartion;

import com.statestreet.carreservation.controller.car.CarDto;
import com.statestreet.carreservation.controller.car.CarMapper;
import com.statestreet.carreservation.controller.user.UserDto;
import com.statestreet.carreservation.controller.user.UserMapper;
import com.statestreet.carreservation.repository.reservation.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    @Autowired
    CarMapper carMapper;

    @Autowired
    UserMapper userMapper;

    ReservationDto toDto(Reservation reservation) {
        CarDto carDto = carMapper.toDto(reservation.getCar());
        UserDto userDto = userMapper.toDto(reservation.getUser());
        return new ReservationDto(reservation.getId(), carDto, userDto);
    }
}
