package com.example.dto;

import lombok.*;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SentenceAndTranslation {
    private String text;
    private String translation;
}
