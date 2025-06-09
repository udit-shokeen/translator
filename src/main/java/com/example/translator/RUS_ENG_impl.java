package com.example.translator;

import com.example.client.LLMModel;
import com.example.dto.LLMModelResponse;
import com.example.dto.TranslatorRequest;
import com.example.dto.TranslatorResponse;
import com.example.enums.Language;
import com.example.enums.TranslatorType;
import com.example.sink.Database;
import com.example.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;


@Slf4j
public class RUS_ENG_impl implements Translator{
    @Override
    public TranslatorResponse translate(TranslatorRequest request, Database database, LLMModel model) {
        List<String> sentences = List.of(request.getText().split("\\. "));
        List<String> translatedTokens = sentences.stream().map(sentence -> {
            String key = TranslatorType.RUS_ENG.name() + "__" + sentence;    // Prefixing with translator type for key creation
            Optional<String> translation = database.getTranslation(key);
            if (translation.isPresent()) {
                return translation.get();
            } else {
                LLMModelResponse translated = model.getTranslationFromModel(sentence, Language.RUS, Language.ENG);
                if(translated.getScore() >= Constants.THRESHOLD) {
//                    log.info("Translation score: {} for sentence: {}, translation: {}", translated.getScore(), sentence, translated.getTranslation());
                    database.addTranslation(key, translated.getTranslation());
                    return translated.getTranslation();
                }
                else{
                    //  TODO :: apply manual validation
                    log.warn("Translation score below threshold: {} for sentence: {}, need manual validation", translated.getScore(), sentence);
                    return Constants.DEFAULT_TRANSLATION;
                }
            }
        }).toList();
        String translatedText = String.join(". ", translatedTokens);
        return TranslatorResponse.builder()
                .translatedText(translatedText)
                .build();
    }
}
