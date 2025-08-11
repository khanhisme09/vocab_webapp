package com.yourname.vocabularyapp.service;

import com.yourname.vocabularyapp.model.ListWord;
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

    /**
     * Tìm tất cả các list của một người dùng.
     * Sử dụng phương thức đã được tối ưu với @EntityGraph để tải luôn thông tin
     * về các từ liên quan, tránh lỗi LazyInitializationException.
     */
    public List<VocabularyList> findListsByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        // Giả định rằng bạn đã cập nhật @EntityGraph để tải 'listWords' thay vì 'words'
        // Trong VocabularyListRepository: @EntityGraph(attributePaths = {"listWords.word"})
        return listRepository.findByUser_Id(user.getId());
    }

    /**
     * Thêm một từ vào một list (tạo list mới nếu cần).
     * Phương thức này đã được cập nhật để tạo một Entity trung gian ListWord.
     */
    @Transactional
    public void addWordToList(String wordText, Long listId, String newListName, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Word word = wordRepository.findByWordText(wordText).orElseGet(() -> wordRepository.save(new Word(wordText)));

        VocabularyList list;
        if (listId != null) {
            // Tải list cùng với các từ đã có để kiểm tra trùng lặp
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

        // Kiểm tra xem từ đã tồn tại trong list hay chưa thông qua Entity trung gian
        boolean alreadyExists = list.getListWords().stream()
                .anyMatch(lw -> lw.getWord().getId().equals(word.getId()));

        if (!alreadyExists) {
            ListWord newListWord = new ListWord(list, word);
            list.getListWords().add(newListWord);
            // Vì list là một managed entity và có CascadeType.ALL, việc lưu list
            // sẽ tự động lưu cả đối tượng ListWord mới.
            listRepository.save(list);
        }
    }
}