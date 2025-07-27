package com.statestreet.carreservation.controller.car;

import com.statestreet.carreservation.service.car.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/cars")
public class CarController {

    private final CarService service;
    private final CarMapper mapper;

    @Autowired
    public CarController(CarService service, CarMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<CarDto> getAllCars(
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int numOfRecords
    ) {
        return mapper.toDtoList(
                service.findAll(sort, Sort.Direction.fromString(order), pageNumber, numOfRecords)
        );
    }

    @GetMapping("/{id}")
    public CarDto getSingleCar(@PathVariable int id) {
        return mapper.toDto(service.getById(id));
    }

    @PostMapping
    public CarDto insert(@RequestBody CarDto car) {
        return mapper.toDto(
                service.insert(mapper.toEntity(car))
        );
    }

    @PutMapping
    public CarDto update(@RequestBody CarDto car) {
        return mapper.toDto(
                service.update(mapper.toEntity(car))
        );
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        return service.delete(id);
    }
}
