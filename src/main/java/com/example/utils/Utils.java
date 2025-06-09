package com.example.utils;

import com.example.enums.Language;
import com.example.enums.TranslatorType;

import java.util.List;


public class Utils {

    public static List<TranslatorType> getTranslatorTypeFromLanguages(Language sourceLanguage, Language targetLanguage) {
        if(!sourceLanguage.equals(Language.ENG) && !targetLanguage.equals(Language.ENG)){
            TranslatorType sourceTranslator = switch (sourceLanguage) {
                case HI -> TranslatorType.HI_ENG;
                case RUS -> TranslatorType.RUS_ENG;
                default -> TranslatorType.NONE;
            };
            TranslatorType targetTranslator = switch (targetLanguage) {
                case HI -> TranslatorType.ENG_HI;
                case RUS -> TranslatorType.ENG_RUS;
                default -> TranslatorType.NONE;
            };
            return List.of(sourceTranslator, targetTranslator);
        }
        else{
            if (sourceLanguage == Language.ENG && targetLanguage == Language.HI) {
                return List.of(TranslatorType.ENG_HI);
            } else if (sourceLanguage == Language.HI) {
                return List.of(TranslatorType.HI_ENG);
            } else if (sourceLanguage == Language.ENG && targetLanguage == Language.RUS) {
                return List.of(TranslatorType.ENG_RUS);
            } else if (sourceLanguage == Language.RUS) {
                return List.of(TranslatorType.RUS_ENG);
            } else {
                return List.of(TranslatorType.NONE);
            }
        }
    }
}
