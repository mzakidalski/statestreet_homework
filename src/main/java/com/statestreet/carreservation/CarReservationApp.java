package com.statestreet.carreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = CarReservationApp.class)
public class CarReservationApp {

    public static void main(String[] args) {
        SpringApplication.run(CarReservationApp.class, args);
    }

}
