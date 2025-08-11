package com.yourname.vocabularyapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhoneticDto {
    private String text; // Phiên âm
    private String audio; // Link file âm thanh
}