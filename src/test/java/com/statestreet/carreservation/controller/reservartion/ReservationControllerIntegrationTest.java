package com.statestreet.carreservation.controller.reservartion;

import com.fasterxml.jackson.databind.JsonNode;
import com.statestreet.carreservation.repository.common.CarType;
import com.statestreet.carreservation.repository.reservation.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Sql(scripts = "/test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ReservationControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ReservationRepository reservationRepository;

    private static final String URL = "http://localhost:{port}/v1/reservations";
    private static final int NUM_OF_CARS_OF_EACH_TYPE = 2;

    LocalDateTime startDate = LocalDateTime.parse("2025-07-12T12:00:00");

    @Test
    @DisplayName("Allow reservation of a car of a given type at a desired date and time for a given\n" +
            "number of days")
    void makeSuccessfullyReservation() {
        ReservatioRequestDto request = new ReservatioRequestDto(1, CarType.SEDAN, startDate, 3);
        var reservationResponse = restTemplate.postForEntity(URL, request, ReservationDto.class,  Map.of("port", port));
        assertThat(reservationResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ReservationDto madeReservation = reservationResponse.getBody();

        assertDatabaseReservationMatchesRequest(madeReservation, request);
    }

    @Test
    void listReservationsIncludesStartAndEndTimes() {
        var request = new ReservatioRequestDto(1, CarType.SEDAN, startDate, 3);
        var created = restTemplate.postForEntity(URL, request, ReservationDto.class, Map.of("port", port));
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(created.getBody()).isNotNull();

        var response = restTemplate.getForEntity(URL + "/user/1", JsonNode.class, Map.of("port", port));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        var content = response.getBody().get("content");
        assertThat(content.size()).isEqualTo(2);
        var reservation = content.get(0);
        assertThat(reservation.get("id").asInt()).isEqualTo(created.getBody().getId());
        assertThat(LocalDateTime.parse(reservation.get("startTime").asText())).isEqualTo(startDate);
        assertThat(LocalDateTime.parse(reservation.get("endTime").asText())).isEqualTo(startDate.plusDays(3));
        for (JsonNode entry : content) {
            var stored = reservationRepository.findById(entry.get("id").asInt()).orElseThrow();
            assertThat(LocalDateTime.parse(entry.get("startTime").asText())).isEqualTo(stored.getStartTime());
            assertThat(LocalDateTime.parse(entry.get("endTime").asText())).isEqualTo(stored.getEndTime());
        }
    }

    @Test
    @DisplayName("No reservation when no cars available for the full period")
    void unsuccessfulReservation() {
        int numOfDays = 10;
        ReservatioRequestDto request = new ReservatioRequestDto(1, CarType.SEDAN, startDate, numOfDays);

        for (int i = 1; i <= NUM_OF_CARS_OF_EACH_TYPE; i++) {
            var reservationResponse = restTemplate.postForEntity(URL, request, ReservationDto.class,
                    Map.of("port", port));
            assertThat(reservationResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        ReservatioRequestDto failRequest = new ReservatioRequestDto(1, CarType.SEDAN, startDate.plusDays(2),
                numOfDays+4);
        var failResponse = restTemplate.postForEntity(URL, failRequest, ReservationDto.class,
                Map.of("port", port));
        assertThat(failResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ReservatioRequestDto otherFailRequest = new ReservatioRequestDto(1, CarType.SEDAN, startDate.minusDays(5),
                numOfDays);
        var otherFailResponse = restTemplate.postForEntity(URL, otherFailRequest, ReservationDto.class,
                Map.of("port", port));
        assertThat(otherFailResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("The number of cars of each type is limited.")
    void limitedNumberOfCars() {

        ReservatioRequestDto request = new ReservatioRequestDto(1, CarType.SEDAN, startDate, 12);

        for (var type : CarType.values()) {
            verifyCarsOfTypeLimited(request, type);
        }
    }

    private void verifyCarsOfTypeLimited(ReservatioRequestDto request, CarType type) {
        request.setCarType(type);
        for (int i = 0; i <= NUM_OF_CARS_OF_EACH_TYPE; i++) {
            var reservationResponse = restTemplate.postForEntity(URL, request, ReservationDto.class,  Map.of("port", port));
            if (i < NUM_OF_CARS_OF_EACH_TYPE) {
                assertThat(reservationResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            } else {
                assertThat(reservationResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void assertDatabaseReservationMatchesRequest(ReservationDto madeReservation, ReservatioRequestDto request) {
        assertThat(madeReservation).isNotNull();
        assertThat(madeReservation.getStartTime()).isEqualTo(request.getStartTime());
        assertThat(madeReservation.getEndTime()).isEqualTo(request.getStartTime().plusDays(request.numOfDays));
        var reservationInDb = reservationRepository.findById(madeReservation.getId()).get();

        assertThat(reservationInDb.getUser().getId()).isEqualTo(request.getUserId());
        assertThat(reservationInDb.getCar().getType()).isEqualTo(request.getCarType());
        assertThat(reservationInDb.getStartTime()).isEqualTo(request.getStartTime());
        assertThat(reservationInDb.getEndTime()).isEqualTo(request.getStartTime().plusDays(request.numOfDays));
    }
}
