package com.example.dto;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TranslatorResponse {
    private String translatedText;
}
