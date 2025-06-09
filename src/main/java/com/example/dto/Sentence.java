package com.example.dto;

import com.example.enums.Language;
import lombok.*;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Sentence {
    private String text;
}
