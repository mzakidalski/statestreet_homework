package com.statestreet.carreservation.repository.car;

import com.statestreet.carreservation.repository.common.CarType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {


    @Query("""
           SELECT c from Car c
           WHERE c.type = :type
           AND NOT EXISTS (
             SELECT r from Reservation r
             WHERE r.car = c AND 
                   r.startTime < :endDate AND 
                   r.endTime < :startDate     
           )                  
        """)
    Optional<Car> findFirstAvailableCarByTypeAndBookingDates(CarType type,
                                                             LocalDateTime startDate,
                                                             LocalDateTime endDate);
}
