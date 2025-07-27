package com.statestreet.carreservation.repository.car;

import com.statestreet.carreservation.repository.common.CarType;
import jakarta.persistence.*;

@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarType type;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String make;

}
