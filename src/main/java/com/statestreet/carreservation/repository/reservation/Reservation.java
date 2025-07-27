package com.statestreet.carreservation.repository.reservation;

import com.statestreet.carreservation.repository.car.Car;
import com.statestreet.carreservation.repository.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    Car car;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;
}
