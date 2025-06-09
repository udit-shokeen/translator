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
import java.util.List;
import java.util.Objects;


@ApplicationScoped
public class TranslatorAdapter {
    @Inject
    LLMModel model;

    private List<Translator> getTranslatorFromLanguages(Language sourceLanguage, Language targetLanguage) {
        List<TranslatorType> types = Utils.getTranslatorTypeFromLanguages(sourceLanguage, targetLanguage);
        return types.stream().map(TranslatorFactory::getTranslatorImpl).toList();
    }

    private Database getDatabase() {
        DB dbConfig = DB.getDBFromString(Constants.DB_IMPL); // This could be dynamic based on application configuration
        return DatabaseFactory.getDatabaseImpl(dbConfig);
    }

    public TranslatorResponse translate(TranslatorRequest request) {
        if (Objects.isNull(request) || Objects.isNull(request.getText()) || request.getText().isEmpty()) {
            return TranslatorResponse.builder().translatedText("").build();
        }

        Database database = getDatabase();
        Language sourceLanguage = request.getSourceLang();
        Language targetLanguage = request.getTargetLang();

        List<Translator> translators = getTranslatorFromLanguages(sourceLanguage, targetLanguage);

        if(translators.size() == 1) {
            return translators.get(0).translate(request, database, model);
        }
        else{
            TranslatorRequest initialRequest = TranslatorRequest.builder()
                    .text(request.getText())
                    .sourceLang(request.getSourceLang())
                    .targetLang(Language.ENG)
                    .build();
            TranslatorResponse intermediateResponse = translators.get(0).translate(initialRequest, database, model);
            TranslatorRequest intermediateRequest = TranslatorRequest.builder()
                    .text(intermediateResponse.getTranslatedText())
                    .sourceLang(Language.ENG)
                    .targetLang(targetLanguage)
                    .build();
            return translators.get(1).translate(intermediateRequest, database, model);
        }
    }

    public void addTranslation(Language sourceLanguage, Language targetLanguage, String sourceText, String translatedText) {
        Database database = getDatabase();
        List<Translator> translators = getTranslatorFromLanguages(sourceLanguage, targetLanguage);
        List<TranslatorType> types = Utils.getTranslatorTypeFromLanguages(sourceLanguage, targetLanguage);

        if(translators.size() == 1) {
            //  add source -> target mapping
            database.addTranslation(types.get(0).name() + "__" + sourceText, translatedText);
        }
        else{
            //  add source -> eng mapping
            TranslatorRequest request = TranslatorRequest.builder()
                    .text(types.get(0).name() + "__" + sourceText)
                    .sourceLang(sourceLanguage)
                    .targetLang(Language.ENG)
                    .build();
            TranslatorResponse intermediateResponse = translators.get(0).translate(request, database, model);
            database.addTranslation(types.get(0).name() + "__" + sourceText, intermediateResponse.getTranslatedText());
            //  add eng -> target mapping
            TranslatorRequest intermediateRequest = TranslatorRequest.builder()
                    .text(intermediateResponse.getTranslatedText())
                    .sourceLang(Language.ENG)
                    .targetLang(targetLanguage)
                    .build();
            TranslatorResponse finalResponse = translators.get(1).translate(intermediateRequest, database, model);
            database.addTranslation(types.get(1).name() + "__" + intermediateResponse.getTranslatedText(), finalResponse.getTranslatedText());
        }
    }

    public void removeLastTranslation(Language sourceLanguage, Language targetLanguage, String sourceText) {
        Database database = getDatabase();
        List<Translator> translators = getTranslatorFromLanguages(sourceLanguage, targetLanguage);
        List<TranslatorType> types = Utils.getTranslatorTypeFromLanguages(sourceLanguage, targetLanguage);

        if(translators.size() == 1) {
            //  remove source -> target mapping
            database.removeLastTranslation(types.get(0).name() + "__" + sourceText);
        }
        else {
            //  remove source -> eng mapping
            TranslatorRequest request = TranslatorRequest.builder()
                    .text(types.get(0).name() + "__" + sourceText)
                    .sourceLang(sourceLanguage)
                    .targetLang(Language.ENG)
                    .build();
            TranslatorResponse intermediateResponse = translators.get(0).translate(request, database, model);
            database.removeLastTranslation(types.get(0).name() + "__" + intermediateResponse.getTranslatedText());
            //  remove eng -> target mapping
            TranslatorRequest intermediateRequest = TranslatorRequest.builder()
                    .text(intermediateResponse.getTranslatedText())
                    .sourceLang(Language.ENG)
                    .targetLang(targetLanguage)
                    .build();
            TranslatorResponse finalResponse = translators.get(1).translate(intermediateRequest, database, model);
            database.removeLastTranslation(types.get(1).name() + "__" + finalResponse.getTranslatedText());
        }
    }
}
