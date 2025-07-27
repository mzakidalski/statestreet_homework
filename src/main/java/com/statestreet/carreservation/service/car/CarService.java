package com.statestreet.carreservation.service.car;

import com.statestreet.carreservation.repository.car.Car;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface CarService {

    List<Car> findAll(String sortField, Sort.Direction direction, int pageNumber, int pageSize);

    Car getById(int id);

    Car insert(Car car);

    Car update(Car car);

    String delete(int id);
}