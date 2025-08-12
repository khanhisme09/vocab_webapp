package com.yourname.vocabularyapp.controller;

import com.yourname.vocabularyapp.model.VocabularyList;
import com.yourname.vocabularyapp.repository.VocabularyListRepository;
import com.yourname.vocabularyapp.service.VocabularyListService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class VocabularyListController {
    private final VocabularyListService listService;
    private final VocabularyListRepository listRepository;

    public VocabularyListController(VocabularyListService listService, VocabularyListRepository listRepository) {
        this.listService = listService;
        this.listRepository = listRepository;
    }

    @PostMapping("/my-lists/add-word")
    public String addWordToList(@RequestParam("word") String word,
                                @RequestParam(value = "listId", required = false) Long listId,
                                @RequestParam(value = "newListName", required = false) String newListName,
                                Principal principal) {
        listService.addWordToList(word, listId, newListName, principal.getName());
        return "redirect:/search?query=" + word;
    }

    @GetMapping("/my-lists")
    public String viewMyLists(Model model, Principal principal) {
        List<VocabularyList> lists = listService.findListsByUsername(principal.getName());
        model.addAttribute("lists", lists);
        return "my-lists";
    }

    @GetMapping("/my-lists/{listId}")
    public String viewListDetails(@PathVariable("listId") Long listId, Model model, Principal principal) {
        VocabularyList list = listRepository.findWithWordsById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));
        if (!list.getUser().getUsername().equals(principal.getName())) {
            throw new SecurityException("Access Denied");
        }
        model.addAttribute("list", list);
        return "list-details";
    }

    @PostMapping("/my-lists/{listId}/remove-word")
    public String removeWordFromList(@PathVariable Long listId, @RequestParam Long wordId, Principal principal) {
        listService.removeWordFromList(listId, wordId, principal.getName());
        return "redirect:/my-lists/" + listId; // Tải lại trang chi tiết list
    }

    //edit list name
    @PostMapping("/my-lists/{listId}/edit-name")
    public String updateListName(@PathVariable Long listId, @RequestParam String newName, Principal principal) {
        listService.updateListName(listId, newName, principal.getName());
        return "redirect:/my-lists/" + listId; // Tải lại trang chi tiết list
    }
}