package com.example.utils;

import com.example.enums.Language;
import com.example.enums.TranslatorType;


public class Utils {

    public static TranslatorType getTranslatorTypeFromLanguages(Language sourceLanguage, Language targetLanguage) {
        if (sourceLanguage == Language.ENG && targetLanguage == Language.HI) {
            return TranslatorType.ENG_HI;
        } else if (sourceLanguage == Language.HI && targetLanguage == Language.ENG) {
            return TranslatorType.HI_ENG;
        } else if (sourceLanguage == Language.ENG && targetLanguage == Language.RUS) {
            return TranslatorType.ENG_RUS;
        } else if (sourceLanguage == Language.RUS && targetLanguage == Language.ENG) {
            return TranslatorType.RUS_ENG;
        } else {
            return TranslatorType.NONE;
        }
    }
}

/*
    package com.example.sink;

import com.example.dto.TranslationMapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class RedisSink implements Database{
    @ConfigProperty(name = "redis.uri", defaultValue = "redis://localhost:6379")
    String redisUri;
    private final RedissonClient redisClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisSink() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisUri);
        this.redisClient = Redisson.create(config);
    }

    @Override
    public Optional<String> getTranslation(String sentence) {
        RBucket<String> bucket = redisClient.getBucket(sentence);
        Optional<String> optionalValue = Optional.ofNullable(bucket.get());
        if(optionalValue.isPresent()) {
            try {
                List<TranslationMapping> mappings = objectMapper.readValue(optionalValue.get(), new TypeReference<>() {});
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
        RBucket<String> bucket = redisClient.getBucket(sentence);
        Optional<String> optionalValue = Optional.ofNullable(bucket.get());
        if(optionalValue.isPresent()) {
            try {
                List<TranslationMapping> mappings = objectMapper.readValue(optionalValue.get(), new TypeReference<>() {});
                mappings.add(
                        TranslationMapping.builder()
                                .mapping(translation)
                                .timestamp(System.currentTimeMillis())
                                .build());
                mappings = mappings.stream()
                        .sorted(Comparator.comparing(TranslationMapping::getTimestamp, Comparator.reverseOrder()))
                        .limit(3)
                        .collect(Collectors.toList());
                bucket.set(objectMapper.writeValueAsString(mappings));
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
                bucket.set(objectMapper.writeValueAsString(mappings));
            }
            catch (JsonProcessingException ex) {
                log.error("error while adding translation", ex);
            }
        }
    }

    @Override
    public void removeLastTranslation(String sentence) {
        RBucket<String> bucket = redisClient.getBucket(sentence);
        Optional<String> optionalValue = Optional.ofNullable(bucket.get());
        if(optionalValue.isPresent()) {
            try {
                List<TranslationMapping> mappings = objectMapper.readValue(optionalValue.get(), new TypeReference<>() {});
                if (!mappings.isEmpty()) {
                    TranslationMapping mapping = mappings.stream().max(Comparator.comparing(TranslationMapping::getTimestamp)).get();
                    mappings.remove(mapping);
                    if (mappings.isEmpty()) {
                        bucket.delete();
                    } else {
                        bucket.set(objectMapper.writeValueAsString(mappings));
                    }
                }
            } catch (JsonProcessingException ex) {
                log.error("error while removing last translation", ex);
            }
        }
    }
}

 */