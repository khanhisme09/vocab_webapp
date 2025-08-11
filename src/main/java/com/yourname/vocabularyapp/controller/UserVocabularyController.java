package com.yourname.vocabularyapp.controller;

import com.yourname.vocabularyapp.model.User;
import com.yourname.vocabularyapp.model.UserVocabulary;
import com.yourname.vocabularyapp.repository.UserRepository;
import com.yourname.vocabularyapp.repository.UserVocabularyRepository;
import com.yourname.vocabularyapp.service.UserVocabularyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.List;

@Controller
public class UserVocabularyController {
    private final UserVocabularyService userVocabularyService;
    private final UserVocabularyRepository userVocabularyRepository;
    private final UserRepository userRepository;


    public UserVocabularyController(UserVocabularyService userVocabularyService, UserVocabularyRepository userVocabularyRepository, UserRepository userRepository) { /* Constructor */
        this.userVocabularyService = userVocabularyService;
        this.userVocabularyRepository = userVocabularyRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/my-vocabulary")
    public String myVocabularyPage(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<UserVocabulary> vocabularyList = userVocabularyRepository.findByUser_Id(user.getId());
        model.addAttribute("vocabularyList", vocabularyList);
        return "my-vocabulary";
    }

    @PostMapping("/my-vocabulary/add")
    public String addWord(@RequestParam("word") String word, Principal principal) {
        // Principal chứa thông tin về người dùng đang đăng nhập
        if (principal != null) {
            userVocabularyService.addWordToUserList(word, principal.getName());
        }
        // Chuyển hướng người dùng về trang tìm kiếm từ đó
        return "redirect:/search?query=" + word;
    }
}