package com.statestreet.carreservation.controller.car;


import com.statestreet.carreservation.repository.car.Car;
import com.statestreet.carreservation.repository.car.CarRepository;
import com.statestreet.carreservation.repository.common.CarType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Sql(scripts = "/test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CarControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CarRepository carRepository;

    private static final String URL = "http://localhost:{port}/v1/cars";
    private static final String URL_WITH_ID = URL + "/{id}";

    @Test
    void retrieveAllCarsWorksCorrectly() {
        ResponseEntity<CarDto[]> response = restTemplate.getForEntity(URL, CarDto[].class, port);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        CarDto[] retrievedCars = response.getBody();
        assertThat(retrievedCars).isNotNull();

        List<CarDto> expectedCars = List.of(
                new CarDto(1, CarType.SEDAN,   "Camry",    "Toyota"),
                new CarDto(2, CarType.SEDAN,   "Accord",   "Honda"),
                new CarDto(3, CarType.SUV,     "Explorer", "Ford"),
                new CarDto(4, CarType.SUV,     "Cherokee", "Jeep"),
                new CarDto(5, CarType.VAN,     "Odyssey",  "Honda"),
                new CarDto(6, CarType.VAN,     "Sprinter", "Mercedes")
        );

        assertThat(List.of(retrievedCars))
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(expectedCars);
    }

    @Test
    void insertCarWorksCorrectly() {
        CarDto request = new CarDto(0, CarType.SEDAN, "Model S", "Tesla");

        ResponseEntity<CarDto> insertResponse = restTemplate.postForEntity(
                URL,
                request,
                CarDto.class,
                Map.of("port", port)
        );

        CarDto createdCar = insertResponse.getBody();
        assertOperationSucceeded(createdCar, insertResponse.getStatusCode());

        ResponseEntity<CarDto> retrieveResponse = restTemplate.getForEntity(
                URL_WITH_ID,
                CarDto.class,
                Map.of("port", port, "id", createdCar.getId())
        );
        CarDto retrievedCar = retrieveResponse.getBody();
        assertOperationSucceeded(retrievedCar, retrieveResponse.getStatusCode());

        assertThat(retrievedCar).usingRecursiveComparison().isEqualTo(createdCar);
    }

    @Test
    void updateCarWorksCorrectly() {

        ResponseEntity<CarDto> retrieveResponse = restTemplate.getForEntity(
                URL_WITH_ID,
                CarDto.class,
                Map.of("port", port, "id", 3)
        );
        CarDto carForUpdate = retrieveResponse.getBody();
        assertOperationSucceeded(carForUpdate, retrieveResponse.getStatusCode());


        carForUpdate.setModel("Explorer XLT");
        carForUpdate.setMake("Ford USA");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CarDto> putEntity = new HttpEntity<>(carForUpdate, headers);

        ResponseEntity<CarDto> updateResponse = restTemplate.exchange(
                URL,
                HttpMethod.PUT,
                putEntity,
                CarDto.class,
                Map.of("port", port)
        );

        CarDto updatedCar = updateResponse.getBody();
        assertOperationSucceeded(updatedCar, updateResponse.getStatusCode());
        assertThat(updatedCar).usingRecursiveComparison().isEqualTo(carForUpdate);
    }

    @Test
    void deleteCarWorksCorrectly() {
        ResponseEntity<CarDto> carFour = restTemplate.getForEntity(
                URL_WITH_ID,
                CarDto.class,
                Map.of("port", port, "id", 4)
        );
        assertThat(carFour.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                URL_WITH_ID,
                HttpMethod.DELETE,
                null,
                Void.class,
                Map.of("port", port, "id", 4)
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<CarDto> afterDelete = restTemplate.getForEntity(
                URL_WITH_ID,
                CarDto.class,
                Map.of("port", port, "id", 4)
        );
        assertThat(afterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private void assertOperationSucceeded(CarDto dto, HttpStatusCode status) {
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNotNull();
        assertThat(status).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("There are 3 types of cars (Sedan, SUV and van).")
    void threeTypesOfCarsPresent() {

        assertThat(carRepository.findAll()
                .stream()
                .map(car -> car.getType())
                .collect(Collectors.toSet()))
                .isEqualTo(Set.of(CarType.SUV, CarType.SEDAN, CarType.VAN));

    }

}