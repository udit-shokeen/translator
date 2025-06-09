package com.example.controller;

import com.example.dto.Sentence;
import com.example.dto.SentenceAndTranslation;
import com.example.dto.TranslatorResponse;
import com.example.dto.TranslatorRequest;
import com.example.enums.Language;
import com.example.enums.TranslatorType;
import com.example.service.TranslatorAdapter;
import com.example.utils.Utils;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Objects;


@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Controller {
    @Inject
    TranslatorAdapter translatorAdapter;

    @POST
    @Path("/translate")
    public TranslatorResponse translate(@QueryParam(value = "sourceLanguage") String sourceLang, @QueryParam(value = "targetLanguage") String targetLang, Sentence sentence) {
        String text = sentence.getText();
        if (Objects.isNull(text) || text.isEmpty()) {
            return TranslatorResponse.builder().translatedText("").build();
        }
        TranslatorRequest request =
                TranslatorRequest.builder()
                        .text(text)
                        .sourceLang(Language.getLanguageFromString(sourceLang))
                        .targetLang(Language.getLanguageFromString(targetLang))
                        .build();
        return translatorAdapter.translate(request);
    }

    @PUT
    @Path("/addTranslation")
    public Response addTranslation(@QueryParam(value = "sourceLanguage") String sourceLang, @QueryParam(value = "targetLanguage") String targetLang, SentenceAndTranslation sentenceAndTranslation) {
        String sentence = sentenceAndTranslation.getText();
        String translation = sentenceAndTranslation.getTranslation();
        if (Objects.isNull(sentence) || sentence.isEmpty() || Objects.isNull(translation) || translation.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sentence and translation must not be null or empty").build();
        }
        Language sourceLanguage = Language.getLanguageFromString(sourceLang);
        Language targetLanguage = Language.getLanguageFromString(targetLang);
        TranslatorType type = Utils.getTranslatorTypeFromLanguages(sourceLanguage, targetLanguage);

        sentence = type.name() + "__" + sentence;
        translatorAdapter.addTranslation(sentence, translation);
        return Response.ok("translation added to database").build();
    }

    @DELETE
    @Path("/deleteLastTranslation")
    public Response removeLastTranslation(@QueryParam(value = "sourceLanguage") String sourceLang, @QueryParam(value = "targetLanguage") String targetLang, Sentence sentenceReq) {
        String sentence = sentenceReq.getText();
        if (Objects.isNull(sentence) || sentence.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sentence must not be null or empty").build();
        }
        Language sourceLanguage = Language.getLanguageFromString(sourceLang);
        Language targetLanguage = Language.getLanguageFromString(targetLang);
        TranslatorType type = Utils.getTranslatorTypeFromLanguages(sourceLanguage, targetLanguage);

        sentence = type.name() + "__" + sentence;
        translatorAdapter.removeLastTranslation(sentence);
        return Response.ok("last translation removed from db").build();
    }
}
