package com.statestreet.carreservation.service.nlp;

import com.statestreet.carreservation.repository.common.CarType;
import com.statestreet.carreservation.repository.nlp.NlpRepositoryType;
import com.statestreet.carreservation.repository.nlp.impl.LlmRepository;
import com.statestreet.carreservation.service.reservation.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.mockito.ArgumentCaptor;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LlmReservationResponseTest {
    private final ChatModel model = mock(ChatModel.class);
    private final ReservationService reservations = mock(ReservationService.class);
    private final NlpReservationService service = new NlpReservationServiceFactory(model)
            .create(NlpRepositoryType.LLM, reservations);

    @ParameterizedTest
    @MethodSource("com.statestreet.carreservation.service.nlp.NlpReservationServiceTest#reservationRequests")
    void passesParsedParametersToReservationService(String text, CarType type,
                                                   LocalDateTime start, LocalDateTime end) throws IOException {
        respond("""
                {"error":false,"carType":"%s","startDate":"%s","endDate":"%s"}
                """.formatted(type, start, end));

        service.makeReservation(1, text);

        verify(reservations).makeReservation(1, type, start, end);
        var prompt = ArgumentCaptor.forClass(Prompt.class);
        verify(model).call(prompt.capture());
        assertThat(prompt.getValue().getInstructions()).hasSize(2);
        assertThat(prompt.getValue().getInstructions().getFirst().getMessageType()).isEqualTo(MessageType.SYSTEM);
        var systemPrompt = new ClassPathResource("prompts/car_reservation_system.prompt")
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(prompt.getValue().getInstructions().getFirst().getText()).isEqualTo(systemPrompt);
        assertThat(prompt.getValue().getInstructions().getLast().getText()).isEqualTo(text);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"error\":true}",
            "{\"error\":true,\"carType\":\"\",\"startDate\":\"\",\"endDate\":\"\"}",
            "{\"error\":false}", "{}", "null", "not JSON", "",
            "{\"error\":false,\"carType\":\"TRUCK\",\"startDate\":\"2026-10-15T10:00:00\",\"endDate\":\"2026-10-18T10:00:00\"}",
            "{\"error\":false,\"carType\":\"SUV\",\"startDate\":\"invalid\",\"endDate\":\"2026-10-18T10:00:00\"}",
            "{\"error\":false,\"carType\":\"SUV\",\"startDate\":\"2026-10-18T10:00:00\",\"endDate\":\"2026-10-15T10:00:00\"}"
    })
    void reportsSimpleParsingError(String json) {
        respond(json);
        assertThatThrownBy(() -> service.makeReservation(1, "invalid request"))
                .isInstanceOf(NlpReservationServiceException.class).hasMessage(LlmRepository.PARSING_ERROR);
        verifyNoInteractions(reservations);
    }

    @Test
    void rejectsBlankInputWithoutCallingModel() {
        assertThatThrownBy(() -> service.makeReservation(1, " "))
                .isInstanceOf(NlpReservationServiceException.class).hasMessage(LlmRepository.PARSING_ERROR);
        verifyNoInteractions(model, reservations);
    }

    private void respond(String json) {
        when(model.call(any(Prompt.class))).thenReturn(new ChatResponse(List.of(new Generation(new AssistantMessage(json)))));
    }
}
