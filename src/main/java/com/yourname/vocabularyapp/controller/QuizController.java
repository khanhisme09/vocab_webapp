package com.yourname.vocabularyapp.controller;

import com.yourname.vocabularyapp.dto.QuizAnswersForm;
import com.yourname.vocabularyapp.dto.QuizQuestionDto;
import com.yourname.vocabularyapp.dto.QuizResultDto;
import com.yourname.vocabularyapp.model.VocabularyList;
import com.yourname.vocabularyapp.service.QuizService;
import com.yourname.vocabularyapp.service.VocabularyListService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class QuizController {

    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

    private final QuizService quizService;
    private final VocabularyListService listService;

    public QuizController(QuizService quizService, VocabularyListService listService) {
        this.quizService = quizService;
        this.listService = listService;
    }

    // Xử lý việc bắt đầu bài quiz
    @PostMapping("/quiz/start")
    public String startQuiz(@RequestParam Long listId, @RequestParam(defaultValue = "10") int questionCount, HttpSession session) {
        try {
            List<QuizQuestionDto> questions = quizService.createQuizForList(listId, questionCount);
            if (questions.isEmpty()) {
                // Xử lý trường hợp không tạo được câu hỏi (ví dụ: list ít hơn 4 từ)
                // Chúng ta sẽ dùng RedirectAttributes để gửi thông báo về trang trước đó
                // (Cần thêm RedirectAttributes vào tham số nếu muốn làm vậy)
                return "redirect:/my-lists"; // Tạm thời chuyển về trang my-lists
            }
            session.setAttribute("quizQuestions", questions);
            // LƯU LẠI listId ĐỂ CÓ THỂ QUAY VỀ ĐÚNG LIST SAU KHI LÀM XONG
            session.setAttribute("currentQuizListId", listId);
            return "redirect:/quiz/do";
        } catch (IllegalArgumentException e) {
            // Tương tự, cần dùng RedirectAttributes để gửi thông báo lỗi
            return "redirect:/my-lists";
        }
    }

    @GetMapping("/quiz/do")
    public String doQuiz(HttpSession session, Model model) {
        List<QuizQuestionDto> questions = (List<QuizQuestionDto>) session.getAttribute("quizQuestions");
        if (questions == null || questions.isEmpty()) {
            return "redirect:/my-lists"; // Nếu không có quiz, quay về trang my-lists
        }
        model.addAttribute("questions", questions);

        // Truyền listId sang để trang kết quả có thể dùng
        model.addAttribute("listId", session.getAttribute("currentQuizListId"));

        return "quiz";
    }


    @PostMapping("/quiz/submit")
    public String submitQuiz(@ModelAttribute QuizAnswersForm form, HttpSession session, Model model) {
        Map<Integer, String> userAnswers = form.getAnswers();
        List<QuizQuestionDto> questions = (List<QuizQuestionDto>) session.getAttribute("quizQuestions");

        QuizResultDto result = quizService.checkAnswers(questions, userAnswers);
        model.addAttribute("result", result);
        model.addAttribute("listId", session.getAttribute("currentQuizListId"));

        session.removeAttribute("quizQuestions");
        session.removeAttribute("currentQuizListId");
        return "quiz-result";
    }
}