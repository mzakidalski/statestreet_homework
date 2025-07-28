package com.statestreet.carreservation.repository.reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    Page<Reservation> findByUserId(int userId, Pageable pageable);

    Page<Reservation> findByCarId(int carId, Pageable pageable);

}
