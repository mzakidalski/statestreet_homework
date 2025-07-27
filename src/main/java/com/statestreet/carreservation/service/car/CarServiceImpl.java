package com.statestreet.carreservation.service.car;

import com.statestreet.carreservation.repository.car.Car;
import com.statestreet.carreservation.repository.car.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
class CarServiceImpl implements CarService {

    private final CarRepository repository;

    @Autowired
    public CarServiceImpl(CarRepository repository) {
        this.repository = repository;
    }

    public List<Car> findAll(String sortField, Sort.Direction direction, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortField));
        return repository.findAll(pageRequest).getContent();
    }

    public Car getById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));
    }

    public Car insert(Car car) {
        car.setId(0);
        return repository.save(car);
    }

    public Car update(Car car) {
        int id = car.getId();
        if (!repository.existsById(id)) {
            throw new CarNotFoundException(id);
        }
        return repository.save(car);
    }

    public String delete(int id) {
        repository.deleteById(id);
        return "Deleted car with id " + id;
    }
}
