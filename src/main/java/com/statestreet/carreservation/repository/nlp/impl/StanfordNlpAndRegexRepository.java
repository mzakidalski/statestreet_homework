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
    private static String CAR_TYPE_MENTION_TYPE = "CAR_TYPE";

    private StanfordCoreNLP pipeline;

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

        return null;
    }

    private ParseResult parseText(List<CoreEntityMention> mentions) {
        var carTypes = new ArrayList<CarType>();
        var datesParsed = new ArrayList<LocalDateTime>();
        var durationParsed = new ArrayList<Duration>();
        var timeEntriesIds = new HashSet<String>();
        var error = false;

        try {
            for (CoreEntityMention currMention : mentions) {
                Timex mentionAsTimeType = currMention
                        .coreMap()
                        .get(TimeAnnotations.TimexAnnotation.class);
                if (CAR_TYPE_MENTION_TYPE.equals(currMention.entityType())) {
                    carTypes.add(parseToCarType(currMention));
                } else if (mentionAsTimeType != null) {
                    error = error || parseTimeRelatedMentions(
                                                    mentionAsTimeType,
                                                    timeEntriesIds,
                                                    datesParsed,
                                                    durationParsed);
                }
            }
        } catch (Exception ex) {
            error = true;
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
