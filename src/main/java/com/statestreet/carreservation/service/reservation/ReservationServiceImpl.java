package com.statestreet.carreservation.service.reservation;

import com.statestreet.carreservation.repository.reservation.Reservation;
import com.statestreet.carreservation.repository.reservation.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    ReservationRepository repository;


    @Override
    public Page<Reservation> findByUserId(int userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable);
    }
}
