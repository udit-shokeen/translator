package com.example.service;

import com.example.client.LLMModel;
import com.example.enums.DB;
import com.example.enums.Language;
import com.example.enums.TranslatorType;
import com.example.sink.Database;
import com.example.sink.DatabaseFactory;
import com.example.translator.Translator;
import com.example.translator.TranslatorFactory;
import com.example.dto.TranslatorResponse;
import com.example.dto.TranslatorRequest;
import com.example.utils.Constants;
import com.example.utils.Utils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Objects;


@ApplicationScoped
public class TranslatorAdapter {
    @Inject
    LLMModel model;

    private Translator getTranslatorFromLanguages(Language sourceLanguage, Language targetLanguage) {
        TranslatorType type = Utils.getTranslatorTypeFromLanguages(sourceLanguage, targetLanguage);
        return TranslatorFactory.getTranslatorImpl(type);
    }

    private Database getDatabase() {
        DB dbConfig = DB.getDBFromString(Constants.DB_IMPL); // This could be dynamic based on application configuration
        return DatabaseFactory.getDatabaseImpl(dbConfig);
    }

    public TranslatorResponse translate(TranslatorRequest request) {
        if (Objects.isNull(request) || Objects.isNull(request.getText()) || request.getText().isEmpty()) {
            return TranslatorResponse.builder().translatedText("").build();
        }

        Language sourceLanguage = request.getSourceLang();
        Language targetLanguage = request.getTargetLang();

        Translator translator = getTranslatorFromLanguages(sourceLanguage, targetLanguage);
        Database database = getDatabase();

        return translator.translate(request, database, model);
    }

    public void addTranslation(String sourceText, String translatedText) {
        Database database = getDatabase();
        database.addTranslation(sourceText, translatedText);
    }

    public void removeLastTranslation(String sourceText) {
        Database database = getDatabase();
        database.removeLastTranslation(sourceText);
    }
}
