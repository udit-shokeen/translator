package com.example.translator;

import com.example.enums.TranslatorType;
import java.util.Map;


public class TranslatorFactory {
    private static final Map<TranslatorType, Translator> translatorImplMap = Map.of(
            TranslatorType.ENG_HI, new ENG_HI_impl(),
            TranslatorType.HI_ENG, new HI_ENG_impl(),
            TranslatorType.ENG_RUS, new ENG_RUS_impl(),
            TranslatorType.RUS_ENG, new RUS_ENG_impl(),
            TranslatorType.NONE, new None()
    );

    public static Translator getTranslatorImpl(TranslatorType config) {
        return translatorImplMap.getOrDefault(config, translatorImplMap.get(TranslatorType.NONE));
    }
}
