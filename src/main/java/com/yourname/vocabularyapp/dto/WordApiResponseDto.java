package com.yourname.vocabularyapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WordApiResponseDto {
    private String word;
    private List<PhoneticDto> phonetics;
    private List<MeaningDto> meanings;
}