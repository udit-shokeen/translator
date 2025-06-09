package com.example.translator;

import com.example.client.LLMModel;
import com.example.dto.TranslatorResponse;
import com.example.dto.TranslatorRequest;
import com.example.sink.Database;


public interface Translator {
    public TranslatorResponse translate(TranslatorRequest request, Database database, LLMModel model);
}