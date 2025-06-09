package com.example.sink;

import com.example.dto.TranslationMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class InMemorySink implements Database{
    private final Map<String, String> translationMapping = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Optional<String> getTranslation(String sentence) {
        if(translationMapping.containsKey(sentence)) {
            try {
                List<TranslationMapping> mappings = objectMapper.readValue(translationMapping.get(sentence), new TypeReference<>() {});
                return mappings.stream().max(Comparator.comparing(TranslationMapping::getTimestamp)).map(TranslationMapping::getMapping);
            }
            catch (JsonProcessingException ex) {
                log.error("error while parsing mappings", ex);
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    @Override
    public void addTranslation(String sentence, String translation) {
        if(translationMapping.containsKey(sentence)) {
            try {
                List<TranslationMapping> mappings = objectMapper.readValue(translationMapping.get(sentence), new TypeReference<>() {});
                mappings.add(
                        TranslationMapping.builder()
                                .mapping(translation)
                                .timestamp(System.currentTimeMillis())
                                .build());
                mappings = mappings.stream()
                        .sorted(Comparator.comparing(TranslationMapping::getTimestamp, Comparator.reverseOrder()))
                        .limit(3)
                        .collect(Collectors.toList());
                translationMapping.put(sentence, objectMapper.writeValueAsString(mappings));
            }
            catch (JsonProcessingException ex) {
                log.error("error while adding translation", ex);
            }
        }
        else{
            try {
                List<TranslationMapping> mappings = new ArrayList<>();
                mappings.add(
                        TranslationMapping.builder()
                                .mapping(translation)
                                .timestamp(System.currentTimeMillis())
                                .build());
                translationMapping.put(sentence, objectMapper.writeValueAsString(mappings));
            }
            catch (JsonProcessingException ex) {
                log.error("error while adding translation", ex);
            }
        }
    }

    @Override
    public void removeLastTranslation(String sentence) {
        if(translationMapping.containsKey(sentence)) {
            try {
                List<TranslationMapping> mappings = objectMapper.readValue(translationMapping.get(sentence), new TypeReference<>() {});
                if (!mappings.isEmpty()) {
                    TranslationMapping mapping = mappings.stream().max(Comparator.comparing(TranslationMapping::getTimestamp)).get();
                    mappings.remove(mapping);
                    if (mappings.isEmpty()) {
                        translationMapping.remove(sentence);
                    } else {
                        translationMapping.put(sentence, objectMapper.writeValueAsString(mappings));
                    }
                }
            } catch (JsonProcessingException ex) {
                log.error("error while removing last translation", ex);
            }
        }
    }
}
