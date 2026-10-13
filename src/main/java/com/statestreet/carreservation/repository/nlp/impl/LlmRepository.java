package com.statestreet.carreservation.repository.nlp.impl;

import com.statestreet.carreservation.repository.nlp.NlpRepository;
import com.statestreet.carreservation.repository.nlp.NlpReservation;
import com.statestreet.carreservation.repository.common.CarType;
import com.statestreet.carreservation.service.nlp.NlpReservationServiceException;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.io.ClassPathResource;

import java.time.LocalDateTime;
import java.util.List;

public class LlmRepository implements NlpRepository {

    public static final String PARSING_ERROR = "Invalid car reservation request";
    private final SystemMessage systemMessage = new SystemMessage(new ClassPathResource("prompts/car_reservation_system.prompt"));

    private final ChatModel chatModel;

    private final BeanOutputConverter<LlmReservationResponse> converter =
            new BeanOutputConverter<>(LlmReservationResponse.class);

    public LlmRepository(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public NlpReservation parse(String text) {
        if (text == null || text.isBlank()) {
            throw new NlpReservationServiceException(PARSING_ERROR);
        }
        try {
            var response = chatModel.call(new Prompt(List.of(
                    systemMessage, new UserMessage(text))));
            var parsed = converter.convert(response.getResult().getOutput().getText());
            if (parsed == null || !Boolean.FALSE.equals(parsed.error()) || parsed.carType() == null
                    || parsed.startDate() == null || parsed.endDate() == null
                    || !parsed.endDate().isAfter(parsed.startDate())) {
                throw new NlpReservationServiceException(PARSING_ERROR);
            }
            return new NlpReservation(parsed.carType(), parsed.startDate(), parsed.endDate());
        } catch (NlpReservationServiceException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new NlpReservationServiceException(PARSING_ERROR, ex);
        }
    }

    public record LlmReservationResponse(Boolean error, CarType carType,
                                         LocalDateTime startDate, LocalDateTime endDate) { }
}
