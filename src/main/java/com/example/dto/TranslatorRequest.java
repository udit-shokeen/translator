package com.example.dto;

import com.example.enums.Language;
import lombok.*;
import java.util.List;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TranslatorRequest {
    private String text;
    private Language sourceLang;
    private Language targetLang;
}
