package com.statestreet.carreservation.controller.reservartion;

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

    @Test
    @DisplayName("allow reservation of a car of a given type at a desired date and time for a given\n" +
            "number of days")
    void makeSuccessfullyReservation() {
        var startDate = LocalDateTime.parse("2025-07-12T12:00:00");
        var request = new ReservatioRequestDto(1, CarType.SEDAN, startDate, 3);
        var postResponse = restTemplate.postForEntity(URL, request, ReservationDto.class,  Map.of("port", port));

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ReservationDto madeReservation = postResponse.getBody();

        assertDatabaseReservationMatchesRequest(madeReservation, request);
    }

    private void assertDatabaseReservationMatchesRequest(ReservationDto madeReservation, ReservatioRequestDto request) {
        var reservationInDb = reservationRepository.findById(madeReservation.getId()).get();
        assertThat(reservationInDb.getUser().getId()).isEqualTo(request.getUserId());
        assertThat(reservationInDb.getCar().getType()).isEqualTo(request.getCarType());
        assertThat(reservationInDb.getStartTime()).isEqualTo(request.getStartTime());
        assertThat(reservationInDb.getEndTime()).isEqualTo(request.getStartTime().plusDays(request.numOfDays));
    }
}