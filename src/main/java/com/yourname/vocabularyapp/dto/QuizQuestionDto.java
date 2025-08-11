package com.yourname.vocabularyapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionDto {
    private String questionText; // Sẽ là một định nghĩa của từ
    private List<String> options; // 4 lựa chọn (từ vựng)
    private String correctAnswer; // Từ đúng (dùng để kiểm tra ở backend)
}