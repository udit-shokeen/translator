package com.example.dto;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TranslationMapping {
    String mapping;
    Long timestamp;
}
