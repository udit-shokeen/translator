package com.example.translator;

import com.example.client.LLMModel;
import com.example.dto.TranslatorRequest;
import com.example.dto.TranslatorResponse;
import com.example.sink.Database;


public class None implements Translator {
    @Override
    public TranslatorResponse translate(TranslatorRequest request, Database database, LLMModel model) {
        // No translation is performed, returning same text
        return new TranslatorResponse(request.getText());
    }
}
