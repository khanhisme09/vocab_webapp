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
//    @GetMapping("/quiz/do")
//    public String doQuiz(HttpSession session, Model model) {
//        List<QuizQuestionDto> questions = (List<QuizQuestionDto>) session.getAttribute("quizQuestions");
//        if (questions == null || questions.isEmpty()) {
//            return "redirect:/quiz";
//        }
//        model.addAttribute("questions", questions);
//        model.addAttribute("quizAnswersForm", new QuizAnswersForm());
//
//        return "quiz";
//    }
    @GetMapping("/quiz/do")
    public String doQuiz(HttpSession session, Model model) {
        Object obj = session.getAttribute("quizQuestions");
        List<QuizQuestionDto> questions = Collections.emptyList();

        if (obj instanceof List<?>) {
            questions = ((List<?>) obj).stream()
                    .filter(QuizQuestionDto.class::isInstance)
                    .map(QuizQuestionDto.class::cast)
                    .toList();
        }

        if (questions.isEmpty()) {
            return "redirect:/quiz";
        }
        model.addAttribute("questions", questions);
        model.addAttribute("quizAnswersForm", new QuizAnswersForm());

        return "quiz";
    }


    @PostMapping("/quiz/submit")
    // THAY ĐỔI Ở ĐÂY: Dùng @ModelAttribute thay vì @RequestParam
    public String submitQuiz(@ModelAttribute QuizAnswersForm form, HttpSession session, Model model) {

        // Lấy Map từ đối tượng form
        Map<Integer, String> userAnswers = form.getAnswers();

        logger.info("Received answers from form object: {}", userAnswers);

        List<QuizQuestionDto> questions = (List<QuizQuestionDto>) session.getAttribute("quizQuestions");

        QuizResultDto result = quizService.checkAnswers(questions, userAnswers);
        model.addAttribute("result", result);

        session.removeAttribute("quizQuestions");
        return "quiz-result";
    }
}