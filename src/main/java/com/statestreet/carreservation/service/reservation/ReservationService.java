package com.statestreet.carreservation.service.reservation;

import com.statestreet.carreservation.repository.common.CarType;
import com.statestreet.carreservation.repository.reservation.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ReservationService {
    Page<Reservation> findByUserId(int userId, Pageable pageable);
    Reservation makeReservation(int userId, CarType type, LocalDateTime startDate, LocalDateTime endDate);


}
