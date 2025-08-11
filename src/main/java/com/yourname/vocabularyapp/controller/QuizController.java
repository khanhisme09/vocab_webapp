package com.yourname.vocabularyapp.controller;

import com.yourname.vocabularyapp.dto.QuizQuestionDto;
import com.yourname.vocabularyapp.dto.QuizResultDto;
import com.yourname.vocabularyapp.model.VocabularyList;
import com.yourname.vocabularyapp.service.QuizService;
import com.yourname.vocabularyapp.service.VocabularyListService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class QuizController {

    private final QuizService quizService;
    private final VocabularyListService listService;

    public QuizController(QuizService quizService, VocabularyListService listService) {
        this.quizService = quizService;
        this.listService = listService;
    }

    // Trang để người dùng chọn list để bắt đầu quiz
    @GetMapping("/quiz")
    public String showQuizHomePage(Model model, Principal principal) {
        List<VocabularyList> lists = listService.findListsByUsername(principal.getName());
        model.addAttribute("lists", lists);
        return "quiz-start";
    }

    // Xử lý việc bắt đầu bài quiz
    @PostMapping("/quiz/start")
    public String startQuiz(@RequestParam Long listId, HttpSession session) {
        try {
            // Tạo bộ câu hỏi và lưu vào session
            List<QuizQuestionDto> questions = quizService.createQuizForList(listId, 10); // Tạo tối đa 10 câu
            session.setAttribute("quizQuestions", questions);
            return "redirect:/quiz/do"; // Chuyển hướng đến trang làm bài
        } catch (IllegalArgumentException e) {
            return "redirect:/quiz?error=" + e.getMessage();
        }
    }

    // Trang làm bài quiz
    @GetMapping("/quiz/do")
    public String doQuiz(HttpSession session, Model model) {
        List<QuizQuestionDto> questions = (List<QuizQuestionDto>) session.getAttribute("quizQuestions");
        if (questions == null || questions.isEmpty()) {
            return "redirect:/quiz"; // Nếu không có câu hỏi, quay lại trang chọn
        }
        model.addAttribute("questions", questions);
        return "quiz";
    }

    // Xử lý việc nộp bài và chấm điểm
    @PostMapping("/quiz/submit")
    public String submitQuiz(@RequestParam Map<Integer, String> answers, HttpSession session, Model model) {
        List<QuizQuestionDto> questions = (List<QuizQuestionDto>) session.getAttribute("quizQuestions");

        QuizResultDto result = quizService.checkAnswers(questions, answers);
        model.addAttribute("result", result);

        session.removeAttribute("quizQuestions"); // Xóa câu hỏi khỏi session sau khi đã chấm
        return "quiz-result";
    }
}