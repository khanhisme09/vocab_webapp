package com.yourname.vocabularyapp.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class QuizAnswersForm {
    // Tên của Map này phải khớp với phần "answers" trong name="answers[...]"
    private Map<Integer, String> answers = new HashMap<>();
}