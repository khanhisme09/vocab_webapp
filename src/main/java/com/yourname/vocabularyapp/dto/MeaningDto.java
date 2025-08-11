package com.yourname.vocabularyapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeaningDto {
    private String partOfSpeech; // Loại từ (danh từ, động từ...)
    private List<DefinitionDto> definitions;
}