package com.statestreet.carreservation.repository.user;

import jakarta.persistence.*;

@Entity
@Table(name = "RESERVATION_USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;
}
