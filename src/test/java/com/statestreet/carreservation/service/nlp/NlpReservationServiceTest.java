package com.statestreet.carreservation.service.nlp;

import com.statestreet.carreservation.repository.common.CarType;
import com.statestreet.carreservation.repository.nlp.NlpRepositoryType;
import com.statestreet.carreservation.service.reservation.ReservationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
class NlpReservationServiceTest {
    @Value("${spring.nlp.repository-type}")
    private NlpRepositoryType repositoryType;

    static Stream<Arguments> reservationRequests() {
        return Stream.of(
                Arguments.of("Reserve an SUV from 15 October 2026 at 10:00 for 3 days",
                        CarType.SUV,
                        LocalDateTime.of(2026, 10, 15, 10, 0),
                        LocalDateTime.of(2026, 10, 18, 10, 0)),
                Arguments.of("Reserve a sedan from 20 October 2026 at 09:00 for 2 days",
                        CarType.SEDAN,
                        LocalDateTime.of(2026, 10, 20, 9, 0),
                        LocalDateTime.of(2026, 10, 22, 9, 0)),
                Arguments.of("Reserve a van from 1 November 2026 at 12:00 for 5 days",
                        CarType.VAN,
                        LocalDateTime.of(2026, 11, 1, 12, 0),
                        LocalDateTime.of(2026, 11, 6, 12, 0))
        );
    }

    //@Disabled("NLP parsing is not implemented yet; the repository returns hardcoded data")
    @ParameterizedTest
    @MethodSource("reservationRequests")
    void passesParsedParametersToReservationService(String text, CarType expectedType,
                                                   LocalDateTime expectedStart, LocalDateTime expectedEnd) {
        var reservationService = mock(ReservationService.class);
        var service = new NlpReservationServiceFactory().create(repositoryType, reservationService);

        service.makeReservation(1, text);

        verify(reservationService).makeReservation(1, expectedType, expectedStart, expectedEnd);
    }
}
