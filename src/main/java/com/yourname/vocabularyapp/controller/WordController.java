package com.yourname.vocabularyapp.controller;

import com.yourname.vocabularyapp.dto.WordApiResponseDto;
import com.yourname.vocabularyapp.service.DictionaryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WordController {

    private final DictionaryService dictionaryService;

    // Spring sẽ tự động "tiêm" một đối tượng DictionaryService vào đây (Dependency Injection)
    public WordController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping("/search")
    public String searchWord(@RequestParam("query") String query, Model model) {
        WordApiResponseDto[] apiResponse = dictionaryService.lookupWord(query);



        // Thêm kết quả vào "model" để Thymeleaf có thể truy cập và hiển thị trên trang HTML
        if (apiResponse != null && apiResponse.length > 0) {
            model.addAttribute("wordData", apiResponse[0]);
            // API có thể trả về nhiều kết quả, ta lấy cái đầu tiên
        } else {
            model.addAttribute("error", "Could not find the word: " + query);
        }
        model.addAttribute("query", query);


        return "word-details"; // Trả về file word-details.html
    }
}