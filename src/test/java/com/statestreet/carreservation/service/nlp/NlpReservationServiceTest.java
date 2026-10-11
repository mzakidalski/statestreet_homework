package com.statestreet.carreservation.service.nlp;

import com.statestreet.carreservation.repository.common.CarType;
import com.statestreet.carreservation.repository.nlp.NlpRepositoryType;
import com.statestreet.carreservation.service.reservation.ReservationService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.statestreet.carreservation.repository.nlp.impl.StanfordNlpAndRegexRepository.*;

@SpringBootTest
class NlpReservationServiceTest {
    @Value("${spring.nlp.repository-type}")
    private NlpRepositoryType repositoryType;

    static Stream<Arguments> reservationRequests() {
        return Stream.of(
                Arguments.of("Book an SUV from 22 October 2039 at 10:00 am for 6 months",
                        CarType.SUV,
                        LocalDateTime.of(2039, 10, 22, 10, 0),
                        LocalDateTime.of(2040, 4, 22, 10, 0)),
                Arguments.of("Book a sedan from 31 August 2040 at 10:00 am for 6 months",
                        CarType.SEDAN,
                        LocalDateTime.of(2040, 8, 31, 10, 0),
                        LocalDateTime.of(2041, 2, 28, 10, 0)),
                Arguments.of("Reserve an SUV from 22 October 2039 at 10:00 am to 25 October 2039 at 3:00 pm",
                        CarType.SUV,
                        LocalDateTime.of(2039, 10, 22, 10, 0),
                        LocalDateTime.of(2039, 10, 25, 15, 0)),
                Arguments.of("Reserve an SUV from 15 October 2026 at 10:00 for 3 days",
                        CarType.SUV,
                        LocalDateTime.of(2026, 10, 15, 10, 0),
                        LocalDateTime.of(2026, 10, 18, 10, 0)),
                Arguments.of("Reserve a sedan from 20 Oct 2026 09:00am for 2 days",
                        CarType.SEDAN,
                        LocalDateTime.of(2026, 10, 20, 9, 0),
                        LocalDateTime.of(2026, 10, 22, 9, 0)),
                Arguments.of("van from 1 Nov, 2026 at noon for 5 days",
                        CarType.VAN,
                        LocalDateTime.of(2026, 11, 1, 12, 0),
                        LocalDateTime.of(2026, 11, 6, 12, 0)),
                Arguments.of("Book sedan from 20 October 2026 09:00 to 22 Oct 2026  10:00",
                        CarType.SEDAN,
                        LocalDateTime.of(2026, 10, 20, 9, 0),
                        LocalDateTime.of(2026, 10, 22, 10, 0)),
                Arguments.of("SUV, 18 October 2026  10:00, 11:00 15 October 2026 ",
                        CarType.SUV,
                        LocalDateTime.of(2026, 10, 15, 11, 0),
                        LocalDateTime.of(2026, 10, 18, 10, 0)),
                Arguments.of("for 5 days  vAn from 00:00 1 Nov 2026  ",
                        CarType.VAN,
                        LocalDateTime.of(2026, 11, 1, 0, 0),
                        LocalDateTime.of(2026, 11, 6, 0, 0)),
                Arguments.of("SUV, 18 October 2025 10:00, 15 October 2025 10:00, ",
                        CarType.SUV,
                        LocalDateTime.of(2025, 10, 15, 10, 0),
                        LocalDateTime.of(2025, 10, 18, 10, 0))
        );
    }

    @ParameterizedTest
    @MethodSource("reservationRequests")
    void passesParsedParametersToReservationService(String text, CarType expectedType,
                                                   LocalDateTime expectedStart, LocalDateTime expectedEnd) {
        var reservationService = mock(ReservationService.class);
        var service = new NlpReservationServiceFactory().create(repositoryType, reservationService);

        service.makeReservation(1, text);

        verify(reservationService).makeReservation(1, expectedType, expectedStart, expectedEnd);
    }

    static Stream<Arguments> invalidRequests() {
        return Stream.of(
                Arguments.of("Reserve a van from 1 November 2026 for 5 days",
                        List.of(INVALID_TEMPORAL_EXPRESSION)),
                Arguments.of("Reserve a car from 15 October 2026 at 10:00 for 3 days", List.of(WRONG_NUMBER_OF_CARS)),
                Arguments.of("Reserve a sedan and an SUV from 15 October 2026 at 10:00 for 3 days",
                        List.of(WRONG_NUMBER_OF_CARS)),
                Arguments.of("Reserve an SUV on 15 October 2026 at 10:00, on 16 October 2026 at 10:00, and on 17 October 2026 at 10:00",
                        List.of(TOO_MANY_DATES, TOO_MANY_DATES_OR_PERIODS)),
                Arguments.of("Reserve an SUV for 2 days and for 3 days", List.of(TOO_MANY_PERIODS)),
                Arguments.of("Reserve an SUV from 15 October 2026 at 10:00 to 18 October 2026 at 10:00 for 3 days",
                        List.of(TOO_MANY_DATES_OR_PERIODS)),
                Arguments.of("Reserve an SUV from 15 October 2026 at 10:00 for 2 days and for 3 days",
                        List.of(TOO_MANY_DATES_OR_PERIODS, TOO_MANY_PERIODS)),
                Arguments.of("Reserve a sedan and an SUV on 15 October 2026 at 10:00, on 16 October 2026 at 10:00, "
                                + "and on 17 October 2026 at 10:00 for 2 days and for 3 days",
                        List.of(WRONG_NUMBER_OF_CARS, TOO_MANY_DATES, TOO_MANY_DATES_OR_PERIODS, TOO_MANY_PERIODS))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void reportsAllApplicableValidationErrors(String text, List<String> expectedMessages) {
        var reservationService = mock(ReservationService.class);
        var service = new NlpReservationServiceFactory().create(repositoryType, reservationService);

        assertThatThrownBy(() -> service.makeReservation(1, text))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(String.join("; ", expectedMessages));
        verifyNoInteractions(reservationService);
    }
}
