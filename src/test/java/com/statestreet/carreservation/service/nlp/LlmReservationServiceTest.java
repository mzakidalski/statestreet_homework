package com.statestreet.carreservation.service.nlp;

import com.statestreet.carreservation.repository.common.CarType;
import com.statestreet.carreservation.repository.nlp.NlpRepositoryType;
import com.statestreet.carreservation.service.reservation.ReservationService;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class LlmReservationServiceTest {
    @Autowired
    private NlpReservationServiceFactory factory;

    @ParameterizedTest
    @MethodSource("com.statestreet.carreservation.service.nlp.NlpReservationServiceTest#reservationRequests")
    void parsesNaturalLanguage(String text, CarType type, LocalDateTime start, LocalDateTime end) {
        var reservations = mock(ReservationService.class);
        factory.create(NlpRepositoryType.LLM, reservations).makeReservation(1, text);
        verify(reservations).makeReservation(1, type, start, end);
    }

}
