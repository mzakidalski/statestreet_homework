package com.statestreet.carreservation.service.reservation;

import com.statestreet.carreservation.repository.reservation.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ReservationService {
    Page<Reservation> findByUserId(int userId, Pageable pageable);
}
