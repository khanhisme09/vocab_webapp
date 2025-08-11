package com.yourname.vocabularyapp.controller;

import com.yourname.vocabularyapp.dto.WordApiResponseDto;
import com.yourname.vocabularyapp.model.VocabularyList;
import com.yourname.vocabularyapp.service.DictionaryService;
import com.yourname.vocabularyapp.service.VocabularyListService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class WordController {

    private final DictionaryService dictionaryService;
    private final VocabularyListService listService;

    public WordController(DictionaryService dictionaryService, VocabularyListService listService) {
        this.dictionaryService = dictionaryService;
        this.listService = listService;
    }

    @GetMapping("/search")
    public String searchWord(@RequestParam("query") String query, Model model, Principal principal) {
        WordApiResponseDto[] apiResponse = dictionaryService.lookupWord(query);

        if (apiResponse != null && apiResponse.length > 0) {
            model.addAttribute("wordData", apiResponse[0]);
        } else {
            model.addAttribute("error", "Could not find the word: " + query);
        }
        model.addAttribute("query", query);

        // Thay đổi ở đây
        if (principal != null) {
            List<VocabularyList> userLists = listService.findListsByUsername(principal.getName());
            model.addAttribute("userLists", userLists);
        }

        return "word-details";
    }
}