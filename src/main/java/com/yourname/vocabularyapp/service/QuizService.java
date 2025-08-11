package com.yourname.vocabularyapp.service;

import com.yourname.vocabularyapp.dto.QuizQuestionDto;
import com.yourname.vocabularyapp.dto.QuizResultDto;
import com.yourname.vocabularyapp.model.VocabularyList;
import com.yourname.vocabularyapp.model.Word;
import com.yourname.vocabularyapp.repository.VocabularyListRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class QuizService {

    private final VocabularyListRepository listRepository;
    private final DictionaryService dictionaryService;

    public QuizService(VocabularyListRepository listRepository, DictionaryService dictionaryService) {
        this.listRepository = listRepository;
        this.dictionaryService = dictionaryService;
    }

    public List<QuizQuestionDto> createQuizForList(Long listId, int numberOfQuestions) {
        VocabularyList list = listRepository.findWithWordsById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        List<Word> allWordsInList = new ArrayList<>(list.getWords());

        // Điều kiện cần để tạo câu hỏi trắc nghiệm
        if (allWordsInList.size() < 4) {
            throw new IllegalArgumentException("List must have at least 4 words to create a quiz.");
        }

        // Xáo trộn danh sách từ để câu hỏi ngẫu nhiên
        Collections.shuffle(allWordsInList);

        List<QuizQuestionDto> quizQuestions = new ArrayList<>();
        int questionsToCreate = Math.min(numberOfQuestions, allWordsInList.size());

        for (int i = 0; i < questionsToCreate; i++) {
            Word correctWord = allWordsInList.get(i);
            String definition = dictionaryService.getFirstDefinition(correctWord.getWordText());

            if (definition == null) continue; // Bỏ qua nếu không tìm thấy định nghĩa

            List<String> options = new ArrayList<>();
            options.add(correctWord.getWordText());

            // Lấy 3 đáp án sai (distractors)
            List<Word> tempWords = new ArrayList<>(allWordsInList);
            tempWords.remove(correctWord); // Bỏ từ đúng ra
            Collections.shuffle(tempWords); // Xáo trộn phần còn lại

            for (int j = 0; j < 3; j++) {
                options.add(tempWords.get(j).getWordText());
            }

            // Xáo trộn các lựa chọn để đáp án đúng không luôn ở vị trí đầu
            Collections.shuffle(options);

            quizQuestions.add(new QuizQuestionDto(definition, options, correctWord.getWordText()));
        }
        return quizQuestions;
    }

    public QuizResultDto checkAnswers(List<QuizQuestionDto> questions, Map<Integer, String> userAnswers) {
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            String correctAnswer = questions.get(i).getCorrectAnswer();
            String userAnswer = userAnswers.get(i);
            if (correctAnswer.equals(userAnswer)) {
                score++;
            }
        }
        return new QuizResultDto(score, questions.size());
    }
}