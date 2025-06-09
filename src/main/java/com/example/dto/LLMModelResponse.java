package com.example.dto;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LLMModelResponse {
    String translation;
    Double score;
}
