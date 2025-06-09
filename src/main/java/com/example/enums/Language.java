package com.example.enums;

import java.util.HashMap;
import java.util.Map;


public enum Language {
    ENG,
    HI,
    RUS;

    private static final Map<String, Language> stringLanguageMap = new HashMap<>();

    static {
        for (Language language : Language.values()) {
            stringLanguageMap.put(language.name().toLowerCase(), language);
        }
    }

    public static Language getLanguageFromString(String language) {
        return stringLanguageMap.getOrDefault(language.toLowerCase(), ENG);
    }
}
