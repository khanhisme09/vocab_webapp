package com.yourname.vocabularyapp.service;

import com.yourname.vocabularyapp.model.User;
import com.yourname.vocabularyapp.model.VocabularyList;
import com.yourname.vocabularyapp.model.Word;
import com.yourname.vocabularyapp.repository.UserRepository;
import com.yourname.vocabularyapp.repository.VocabularyListRepository;
import com.yourname.vocabularyapp.repository.WordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class VocabularyListService {

    private final VocabularyListRepository listRepository;
    private final WordRepository wordRepository;
    private final UserRepository userRepository;

    public VocabularyListService(VocabularyListRepository listRepository, WordRepository wordRepository, UserRepository userRepository) {
        this.listRepository = listRepository;
        this.wordRepository = wordRepository;
        this.userRepository = userRepository;
    }

    public List<VocabularyList> findListsByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return listRepository.findByUser_Id(user.getId());
    }

    @Transactional
    public void addWordToList(String wordText, Long listId, String newListName, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Word word = wordRepository.findByWordText(wordText).orElseGet(() -> wordRepository.save(new Word(wordText)));

        VocabularyList list;
        if (listId != null) {

            list = listRepository.findWithWordsById(listId)
                    .orElseThrow(() -> new RuntimeException("List not found"));

            if (!list.getUser().getId().equals(user.getId())) {
                throw new SecurityException("User does not have permission to access this list");
            }
        } else if (newListName != null && !newListName.isBlank()) {
            list = new VocabularyList(newListName, user);
        } else {
            throw new IllegalArgumentException("Must provide either an existing list ID or a new list name.");
        }

        list.getWords().add(word);
        listRepository.save(list);
    }
}