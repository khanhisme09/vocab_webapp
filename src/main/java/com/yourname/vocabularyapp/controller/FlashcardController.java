package com.yourname.vocabularyapp.controller;

import com.yourname.vocabularyapp.dto.FlashcardDto;
import com.yourname.vocabularyapp.model.VocabularyList;
import com.yourname.vocabularyapp.repository.VocabularyListRepository;
import com.yourname.vocabularyapp.service.DictionaryService; // <-- IMPORT MỚI
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FlashcardController {

    private final VocabularyListRepository listRepository;
    private final DictionaryService dictionaryService; // <-- INJECT SERVICE MỚI

    public FlashcardController(VocabularyListRepository listRepository, DictionaryService dictionaryService) {
        this.listRepository = listRepository;
        this.dictionaryService = dictionaryService;
    }

    @GetMapping("/lists/{listId}/flashcards")
    public String showFlashcards(@PathVariable Long listId, Model model, Principal principal) {
        VocabularyList list = listRepository.findWithWordsById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        // TẠO MỘT LIST DTO MỚI
        List<FlashcardDto> flashcardDtos = new ArrayList<>();

        // LẶP QUA CÁC TỪ TRONG LIST VÀ CHUYỂN ĐỔI SANG DTO
        for (var listWord : list.getListWords()) {
            String word = listWord.getWord().getWordText();
            String definition = dictionaryService.getFirstDefinition(word);

            // Chỉ thêm vào flashcard nếu có cả từ và định nghĩa
            if (definition != null) {
                flashcardDtos.add(new FlashcardDto(word, definition));
            }
        }

        // TRUYỀN DTO VÀ CÁC THÔNG TIN CẦN THIẾT SANG VIEW
        model.addAttribute("flashcards", flashcardDtos);
        model.addAttribute("listName", list.getName());
        model.addAttribute("listId", list.getId());

        return "flashcards";
    }
}