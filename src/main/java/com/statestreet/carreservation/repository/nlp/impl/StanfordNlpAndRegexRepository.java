package com.statestreet.carreservation.repository.nlp.impl;

import com.statestreet.carreservation.repository.common.CarType;
import com.statestreet.carreservation.repository.nlp.NlpRepository;
import com.statestreet.carreservation.repository.nlp.NlpReservation;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.time.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class StanfordNlpAndRegexRepository implements NlpRepository {

    private static final String CAR_TYPE_MENTION_TYPE = "CAR_TYPE";

    public static final String WRONG_NUMBER_OF_CARS = "Wrong number of cars specified.";
    public static final String TOO_MANY_DATES = "Too many dates mentioned in the request";
    public static final String TOO_MANY_DATES_OR_PERIODS = "Too many dates or periods mentioned in the request";
    public static final String TOO_MANY_PERIODS = "Too many periods mentioned in the request";
    public static final String INVALID_TEMPORAL_EXPRESSION =
            "Cannot parse date or period mentioned in the request. Maybe you forgot to specify the hour?";

    private static final int EXPECTED_NUMBER_OF_CARS = 1;
    private static final int MAX_NUMBER_OF_DATES = 2;
    private static final int MAX_NUMBER_OF_DATES_AND_DURATIONS = 3;
    private static final int MAX_NUMBER_OF_DURATIONS = 2;

    private final StanfordCoreNLP pipeline;

    private record ParseResult(boolean error,
                               List<CarType> carTypes,
                               List<LocalDateTime> datesParsed,
                               List<Duration> durationParsed) {};

    public StanfordNlpAndRegexRepository() {
        Properties props = createProperties();

        this.pipeline = new StanfordCoreNLP(props);
    }

    @Override
    public NlpReservation parse(String text) {
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document);
        ParseResult parseResult = parseText(document.entityMentions());
        validateParesedResults(parseResult);
        return toReservation(parseResult);
    }

    private NlpReservation toReservation(ParseResult result) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (result.datesParsed().size() == 2) {
            startTime = Collections.min(result.datesParsed());
            endTime = Collections.max(result.datesParsed());
        } else if (result.datesParsed().size() == 1) {
            startTime = result.datesParsed().getFirst();
            if (result.durationParsed().size() == 1) {
                endTime = startTime.plus(result.durationParsed().getFirst());
            }
        }

        return new NlpReservation(result.carTypes().getFirst(), startTime, endTime);
    }

    private void validateParesedResults(ParseResult result) {
        List<String> errors = new ArrayList<>();
        if (result.carTypes().size() != EXPECTED_NUMBER_OF_CARS) {
            errors.add(WRONG_NUMBER_OF_CARS);
        }
        if (result.datesParsed().size() > MAX_NUMBER_OF_DATES) {
            errors.add(TOO_MANY_DATES);
        }
        if (result.durationParsed().size() + result.datesParsed().size() >= MAX_NUMBER_OF_DATES_AND_DURATIONS) {
            errors.add(TOO_MANY_DATES_OR_PERIODS);
        }
        if (result.durationParsed().size() >= MAX_NUMBER_OF_DURATIONS) {
            errors.add(TOO_MANY_PERIODS);
        }
        if (result.error()) {
            errors.add(INVALID_TEMPORAL_EXPRESSION);
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }
    }

    private ParseResult parseText(List<CoreEntityMention> mentions) {
        var carTypes = new ArrayList<CarType>();
        var datesParsed = new ArrayList<LocalDateTime>();
        var durationParsed = new ArrayList<Duration>();
        var timeEntriesIds = new HashSet<String>();
        var error = false;

        for (CoreEntityMention currMention : mentions) {
            try {
                Timex mentionAsTimeType = currMention
                        .coreMap()
                        .get(TimeAnnotations.TimexAnnotation.class);
                if (CAR_TYPE_MENTION_TYPE.equals(currMention.entityType())) {
                    carTypes.add(parseToCarType(currMention));
                } else if (mentionAsTimeType != null) {
                    error = parseTimeRelatedMentions(
                                                    mentionAsTimeType,
                                                    timeEntriesIds,
                                                    datesParsed,
                                                    durationParsed) || error;
                }
            } catch (IllegalArgumentException | java.time.DateTimeException ex) {
                error = true;
            }
        }

        return new ParseResult(error, carTypes, datesParsed, durationParsed);
    }

    private boolean parseTimeRelatedMentions(Timex mentionAsTimeType,
                                          Set<String> timeEntriesIds,
                                          List<LocalDateTime> datesParsed,
                                          List<Duration> durationParsed) {

        String value = mentionAsTimeType.value();
        if (value == null || value.isBlank()) {
            return true;
        }
        if (timeEntriesIds.contains(mentionAsTimeType.tid())) {
            return false;
        } else {
            timeEntriesIds.add(mentionAsTimeType.tid());
        }

        switch (mentionAsTimeType.timexType()) {
            case "DATE", "TIME" -> {
                datesParsed.add(LocalDateTime.parse(value));
            }
            case "DURATION" -> {
                Duration duration = Duration.parse(value);
                if (duration.isZero() || duration.isNegative()) {
                   return true;
                } else {
                    durationParsed.add(duration);
                }
            }
            default -> {
                return true;
            }
        }

        return false;
    }

    private static CarType parseToCarType(CoreEntityMention currMention) {
        return CarType.valueOf(currMention.text().trim().toUpperCase(Locale.ROOT));
    }

    private Properties createProperties() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,pos,lemma,ner");
        props.setProperty("ner.additional.regexner.mapping", "car-types.rules");
        props.setProperty("ner.additional.regexner.ignorecase", "true");
        props.setProperty("ner.rulesOnly", "true");
        props.setProperty("ner.applyFineGrained", "false");
        return props;
    }
}
