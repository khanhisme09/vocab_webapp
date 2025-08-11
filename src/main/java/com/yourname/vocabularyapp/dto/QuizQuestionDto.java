package com.yourname.vocabularyapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionDto {

    public enum QuestionType {
        MULTIPLE_CHOICE,
        FILL_IN_THE_BLANK
    }

    private Long listWordId; // ID của bản ghi trong bảng nối
    private QuestionType type;
    private String questionText;
    private List<String> options;
    private String correctAnswer;
}