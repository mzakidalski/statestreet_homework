package com.statestreet.carreservation.service.nlp;

import com.statestreet.carreservation.repository.common.CarType;
import com.statestreet.carreservation.repository.nlp.NlpRepository;
import com.statestreet.carreservation.repository.nlp.NlpReservation;
import com.statestreet.carreservation.repository.nlp.NlpRepositoryType;
import com.statestreet.carreservation.repository.nlp.impl.HardcodedNlpRepository;
import com.statestreet.carreservation.service.nlp.impl.NlpReservationServiceImpl;
import com.statestreet.carreservation.service.reservation.ReservationService;
import com.statestreet.carreservation.service.user.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static com.statestreet.carreservation.service.nlp.NlpReservationService.LACK_OF_CAR_TYPE;
import static com.statestreet.carreservation.service.nlp.NlpReservationService.LACK_OF_RESERVATION_START_DATE;
import static com.statestreet.carreservation.service.nlp.NlpReservationService.LACK_OF_RESERVATION_DURATION;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NlpReservationServiceFlowTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 10, 15, 10, 0);

    static Stream<Arguments> missingParameters() {
        return Stream.of(
                Arguments.of(new NlpReservation(null, START, 3), List.of(LACK_OF_CAR_TYPE)),
                Arguments.of(new NlpReservation(CarType.SUV, null, 3), List.of(LACK_OF_RESERVATION_START_DATE)),
                Arguments.of(new NlpReservation(CarType.SUV, START, null), List.of(LACK_OF_RESERVATION_DURATION)),
                Arguments.of(new NlpReservation(CarType.SUV, START, 0), List.of(LACK_OF_RESERVATION_DURATION)),
                Arguments.of(new NlpReservation(CarType.SUV, START, -1), List.of(LACK_OF_RESERVATION_DURATION)),
                Arguments.of(new NlpReservation(null, null, 3),
                        List.of(LACK_OF_CAR_TYPE, LACK_OF_RESERVATION_START_DATE)),
                Arguments.of(new NlpReservation(null, START, null),
                        List.of(LACK_OF_CAR_TYPE, LACK_OF_RESERVATION_DURATION)),
                Arguments.of(new NlpReservation(CarType.SUV, null, 0),
                        List.of(LACK_OF_RESERVATION_START_DATE, LACK_OF_RESERVATION_DURATION)),
                Arguments.of(new NlpReservation(null, null, null),
                        List.of(LACK_OF_CAR_TYPE, LACK_OF_RESERVATION_START_DATE, LACK_OF_RESERVATION_DURATION)),
                Arguments.of(new NlpReservation(null, null, -1),
                        List.of(LACK_OF_CAR_TYPE, LACK_OF_RESERVATION_START_DATE, LACK_OF_RESERVATION_DURATION))
        );
    }

    @ParameterizedTest
    @MethodSource("missingParameters")
    void reportsAllValidationErrors(NlpReservation parsed, List<String> expectedMessages) {
        var repository = mock(NlpRepository.class);
        var reservations = mock(ReservationService.class);
        when(repository.parse("text")).thenReturn(parsed);
        var service = new NlpReservationServiceImpl(repository, reservations);

        assertThatThrownBy(() -> service.makeReservation(1, "text"))
                .isInstanceOf(NlpReservationServiceException.class)
                .hasMessage(String.join("; ", expectedMessages));
        verifyNoInteractions(reservations);
    }

    @Test
    void wrapsReservationServiceException() {
        var reservations = mock(ReservationService.class);
        var cause = new UserNotFoundException(100);
        when(reservations.makeReservation(anyInt(), any(), any(), any())).thenThrow(cause);
        var service = new NlpReservationServiceFactory().create(NlpRepositoryType.REGEX, reservations);

        assertThatThrownBy(() -> service.makeReservation(100, "text"))
                .isInstanceOf(NlpReservationServiceException.class).hasMessage(cause.getMessage()).hasCause(cause);
    }

    @ParameterizedTest
    @EnumSource(NlpRepositoryType.class)
    void usesHardcodedRepositoryForEveryStrategy(NlpRepositoryType type) {
        var factory = new NlpReservationServiceFactory();
        var reservations = mock(ReservationService.class);
        var service = factory.create(type, reservations);

        assertThat(service).isExactlyInstanceOf(NlpReservationServiceImpl.class);
        assertThat(factory.createRepository(type)).isExactlyInstanceOf(HardcodedNlpRepository.class);

        service.makeReservation(1, "text");

        verify(reservations).makeReservation(1, CarType.SUV, START, START.plusDays(3));
    }
}
