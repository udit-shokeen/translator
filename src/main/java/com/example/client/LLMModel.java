package com.example.client;

import com.example.dto.LLMModelResponse;
import com.example.enums.Language;
import jakarta.inject.Singleton;
import java.util.Random;


@Singleton
public class LLMModel {
    public LLMModelResponse getTranslationFromModel(String sentence, Language sourceLang, Language targetLang) {
        // Simulate a call to an LLM service to get the translation
        // In a real application, this would involve making an HTTP request to the LLM API
        return LLMModelResponse.builder()
                .translation("translatedFrom " + sourceLang + " to " + targetLang + ": " + sentence)
                .score(new Random().nextDouble()) // Simulating a score for the translation
                .build();
    }
}
