package com.statestreet.carreservation.controller.nlp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class NlpReservationControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void makeReservationSuccessfully() {
        var request = new NlpReservationRequestDto(1, "Reserve an SUV from 15 October 2026 at 10:00 for 3 days");
        var response = restTemplate.postForEntity("/nlpReservation", request, NlpReservationDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStartTime()).isEqualTo(LocalDateTime.of(2026, 10, 15, 10, 0));
        assertThat(response.getBody().getEndTime()).isEqualTo(LocalDateTime.of(2026, 10, 18, 10, 0));
    }
}
