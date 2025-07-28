package com.statestreet.carreservation.service.reservation;

import com.statestreet.carreservation.repository.car.CarRepository;
import com.statestreet.carreservation.repository.common.CarType;
import com.statestreet.carreservation.repository.reservation.Reservation;
import com.statestreet.carreservation.repository.reservation.ReservationRepository;
import com.statestreet.carreservation.repository.user.UserRepository;
import com.statestreet.carreservation.service.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    ReservationRepository reservationRepo;

    @Autowired
    CarRepository carRepo;

    @Autowired
    UserRepository userRepo;


    @Override
    public Page<Reservation> findByUserId(int userId, Pageable pageable) {
        return reservationRepo.findByUserId(userId, pageable);
    }

    @Override
    public Reservation makeReservation(int userId, CarType type, LocalDateTime startDate, LocalDateTime endDate) {
       var car = carRepo.findFirstAvailableCarByTypeAndBookingDates(type, startDate, endDate)
               .orElseThrow(() -> new CarReservationException(startDate, endDate, type));
       var user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

       Reservation reservation = new Reservation();
       reservation.setId(0);
       reservation.setUser(user);
       reservation.setCar(car);

       return reservationRepo.save(reservation);
    }
}
