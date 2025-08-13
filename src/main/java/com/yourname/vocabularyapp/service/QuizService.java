package com.yourname.vocabularyapp.service;

import com.yourname.vocabularyapp.dto.QuizQuestionDto;
import com.yourname.vocabularyapp.dto.QuizResultDto;
import com.yourname.vocabularyapp.model.ListWord;
import com.yourname.vocabularyapp.model.VocabularyList;
import com.yourname.vocabularyapp.model.Word;
import com.yourname.vocabularyapp.repository.VocabularyListRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);

    private final VocabularyListRepository listRepository;
    private final DictionaryService dictionaryService;
    private final ReviewService reviewService; // Inject ReviewService

    public QuizService(VocabularyListRepository listRepository, DictionaryService dictionaryService, ReviewService reviewService) {
        this.listRepository = listRepository;
        this.dictionaryService = dictionaryService;
        this.reviewService = reviewService;
    }

    public List<QuizQuestionDto> createQuizForList(Long listId, int maxQuestions) {
        VocabularyList list = listRepository.findWithWordsById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        List<Word> allWordsInList = list.getListWords().stream()
                .map(ListWord::getWord)
                .collect(Collectors.toList());

        // Điều kiện tối thiểu để tạo quiz vẫn cần thiết
        if (allWordsInList.size() < 4) {
            throw new IllegalArgumentException("List must have at least 4 words to create a quiz.");
        }

        // Xáo trộn danh sách từ để câu hỏi luôn mới mẻ
        Collections.shuffle(allWordsInList);

        List<QuizQuestionDto> quizQuestions = new ArrayList<>();

        // Lặp qua TOÀN BỘ danh sách từ đã xáo trộn
        for (Word correctWord : allWordsInList) {
            // Dừng lại khi đã tạo đủ số câu hỏi mong muốn
            if (quizQuestions.size() >= maxQuestions) {
                break;
            }

            QuizQuestionDto newQuestion = null;

            // Quyết định ngẫu nhiên loại câu hỏi
            if (Math.random() > 0.5) {
                // Thử tạo câu hỏi trắc nghiệm
                String definition = dictionaryService.getFirstDefinition(correctWord.getWordText());
                if (definition != null) {
                    List<String> options = new ArrayList<>();
                    options.add(correctWord.getWordText());

                    List<Word> tempWords = new ArrayList<>(allWordsInList);
                    tempWords.remove(correctWord);
                    Collections.shuffle(tempWords);

                    for (int j = 0; j < 3; j++) {
                        options.add(tempWords.get(j).getWordText());
                    }
                    Collections.shuffle(options);
                    newQuestion = new QuizQuestionDto(correctWord.getListWords().iterator().next().getId(), QuizQuestionDto.QuestionType.MULTIPLE_CHOICE, definition, options, correctWord.getWordText());
                }
            } else {
                // Thử tạo câu hỏi điền vào chỗ trống
                String example = dictionaryService.getFirstExample(correctWord.getWordText());
                if (example != null) {
                    String questionSentence = example.replaceAll("(?i)" + correctWord.getWordText(), "_______");
                    newQuestion = new QuizQuestionDto(correctWord.getListWords().iterator().next().getId(), QuizQuestionDto.QuestionType.FILL_IN_THE_BLANK, questionSentence, null, correctWord.getWordText());
                }
            }

            // Nếu tạo câu hỏi thành công (không bị null), thêm vào danh sách
            if (newQuestion != null) {
                quizQuestions.add(newQuestion);
            }
        }

        return quizQuestions;
    }

    // Phương thức này giờ sẽ gọi ReviewService để cập nhật SRS
    public QuizResultDto checkAnswers(List<QuizQuestionDto> questions, Map<Integer, String> userAnswers) {
        int score = 0;
        logger.info("Starting to check answers. Total questions: {}. User answers received: {}", questions.size(), userAnswers.size());

        for (int i = 0; i < questions.size(); i++) {
            QuizQuestionDto question = questions.get(i);
            String correctAnswer = question.getCorrectAnswer();
            String userAnswer = userAnswers.get(i);

            // Luôn kiểm tra null trước khi thực hiện các thao tác trên chuỗi
            boolean isCorrect = false;
            if (userAnswer != null) {
                // 1. Dùng trim() để loại bỏ khoảng trắng thừa ở đầu và cuối.
                // 2. Dùng equalsIgnoreCase() để so sánh không phân biệt viết hoa/thường.
                isCorrect = correctAnswer.equalsIgnoreCase(userAnswer.trim());
            }

            if (isCorrect) {
                score++;
            }

            // Ghi log để gỡ lỗi, rất hữu ích
            logger.info("Q{}: Correct='{}', User='{}', Match={}", i + 1, correctAnswer, userAnswer, isCorrect);

            // Cập nhật trạng thái SRS (đã có từ trước)
            if (question.getListWordId() != null) {
                reviewService.processAnswer(question.getListWordId(), isCorrect);
            }
        }
        logger.info("Final score: {}/{}", score, questions.size());
        return new QuizResultDto(score, questions.size());
    }
}