package com.yourname.vocabularyapp.controller;

import com.yourname.vocabularyapp.dto.FlashcardDto;
import com.yourname.vocabularyapp.model.VocabularyList;
import com.yourname.vocabularyapp.model.Word; // Import thêm
import com.yourname.vocabularyapp.repository.VocabularyListRepository;
// BỎ DictionaryService vì không cần gọi API nữa
// import com.yourname.vocabularyapp.service.DictionaryService;
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
    // Không cần DictionaryService nữa vì mọi dữ liệu đã được lưu trong CSDL

    public FlashcardController(VocabularyListRepository listRepository) {
        this.listRepository = listRepository;
    }

    @GetMapping("/lists/{listId}/flashcards")
    public String showFlashcards(@PathVariable Long listId, Model model, Principal principal) {
        VocabularyList list = listRepository.findWithWordsById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        List<FlashcardDto> flashcardDtos = new ArrayList<>();

        // LẶP QUA CÁC TỪ VÀ LẤY DỮ LIỆU ĐÃ LƯU TRONG CSDL
        for (var listWord : list.getListWords()) {
            Word word = listWord.getWord();

            // Chỉ thêm vào flashcard nếu có định nghĩa
            if (word.getCachedDefinition() != null && !word.getCachedDefinition().isBlank()) {
                flashcardDtos.add(new FlashcardDto(
                        word.getWordText(),
                        word.getCachedDefinition(),
                        word.getPhonetic(),     // Lấy phiên âm đã lưu
                        word.getAudioUrl()      // Lấy link audio đã lưu
                ));
            }
        }

        model.addAttribute("flashcards", flashcardDtos);
        model.addAttribute("listName", list.getName());
        model.addAttribute("listId", list.getId());

        return "flashcards";
    }
}