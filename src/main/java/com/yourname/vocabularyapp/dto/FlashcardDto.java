package com.yourname.vocabularyapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardDto {
    private String wordText;
    private String definition;
    // THÊM 2 TRƯỜNG MỚI
    private String phonetic;
    private String audioUrl;
}