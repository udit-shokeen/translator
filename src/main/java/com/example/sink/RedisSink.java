package com.example.sink;

import com.example.dto.TranslationMapping;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.mutiny.redis.client.Redis;
import io.vertx.mutiny.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class RedisSink implements Database {

    private final RedisAPI redisClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisSink() {
        Vertx vertx = Vertx.vertx();
        RedisOptions options = new RedisOptions().setConnectionString("redis://localhost:6379");
        Redis redis = Redis.createClient(io.vertx.mutiny.core.Vertx.newInstance(vertx), options);
        this.redisClient = RedisAPI.api(redis);
    }

    @Override
    public Optional<String> getTranslation(String sentence) {
        try {
            var response = redisClient.get(sentence).await().indefinitely();
            if (Objects.isNull(response))
                return Optional.empty();

            List<TranslationMapping> mappings = objectMapper.readValue(response.toString(), new TypeReference<>() {});
            return mappings.stream()
                    .max(Comparator.comparing(TranslationMapping::getTimestamp))
                    .map(TranslationMapping::getMapping);

        } catch (Exception ex) {
            log.error("Error while fetching translation from Redis", ex);
            return Optional.empty();
        }
    }

    @Override
    public void addTranslation(String sentence, String translation) {
        try {
            var response = redisClient.get(sentence).await().indefinitely();
            List<TranslationMapping> mappings;
            if (Objects.nonNull(response)) {
                mappings = objectMapper.readValue(response.toString(), new TypeReference<>() {});
            } else {
                mappings = new ArrayList<>();
            }

            mappings.add(
                    TranslationMapping.builder()
                            .mapping(translation)
                            .timestamp(System.currentTimeMillis())
                            .build());

            mappings = mappings.stream()
                    .sorted(Comparator.comparing(TranslationMapping::getTimestamp).reversed())
                    .limit(3)
                    .collect(Collectors.toList());

            String mappingString = objectMapper.writeValueAsString(mappings);
            redisClient.setex(sentence, "3600", mappingString).await().indefinitely();
        } catch (Exception ex) {
            log.error("Error while adding translation to Redis", ex);
        }
    }

    @Override
    public void removeLastTranslation(String sentence) {
        try {
            var response = redisClient.get(sentence).await().indefinitely();
            if (Objects.isNull(response))
                return;

            List<TranslationMapping> mappings = objectMapper.readValue(response.toString(), new TypeReference<>() {});
            if (mappings.isEmpty())
                return;

            TranslationMapping latest = mappings.stream()
                    .max(Comparator.comparing(TranslationMapping::getTimestamp))
                    .orElse(null);

            mappings.remove(latest);
            if (mappings.isEmpty()) {
                redisClient.del(List.of(sentence)).await().indefinitely();
            } else {
                String updatedJson = objectMapper.writeValueAsString(mappings);
                redisClient.setex(sentence, "3600", updatedJson).await().indefinitely();
            }

        } catch (Exception ex) {
            log.error("Error while removing translation from Redis", ex);
        }
    }
}
