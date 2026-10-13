package com.statestreet.carreservation.service.nlp;

import com.statestreet.carreservation.repository.nlp.NlpRepository;
import com.statestreet.carreservation.repository.nlp.NlpRepositoryType;
import com.statestreet.carreservation.repository.nlp.impl.HardcodedNlpRepository;
import com.statestreet.carreservation.repository.nlp.impl.LlmRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import com.statestreet.carreservation.repository.nlp.impl.StanfordNlpAndRegexRepository;
import com.statestreet.carreservation.service.nlp.impl.NlpReservationServiceImpl;
import com.statestreet.carreservation.service.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NlpReservationServiceFactory {
    private final ChatModel chatModel;

    public NlpReservationServiceFactory() {
        this(null);
    }

    @Autowired
    public NlpReservationServiceFactory(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public NlpReservationService create(NlpRepositoryType type, ReservationService reservationService) {
        return new NlpReservationServiceImpl(createRepository(type), reservationService);
    }

    public NlpRepository createRepository(NlpRepositoryType type) {
        return switch (type) {
            case STANFORD_NLP_AND_REGEX -> new StanfordNlpAndRegexRepository();
            case REGEX -> new HardcodedNlpRepository();
            case LLM -> new LlmRepository(chatModel);
        };
    }

    @Bean
    public NlpReservationService nlpReservationService(@Value("${spring.nlp.repository-type}") NlpRepositoryType type,
                                                      ReservationService reservationService) {
        return create(type, reservationService);
    }
}
