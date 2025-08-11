package com.yourname.vocabularyapp.service;

import com.yourname.vocabularyapp.model.User;
import com.yourname.vocabularyapp.model.Word;
import com.yourname.vocabularyapp.model.UserVocabulary;
import com.yourname.vocabularyapp.repository.UserRepository;
import com.yourname.vocabularyapp.repository.WordRepository;
import com.yourname.vocabularyapp.repository.UserVocabularyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserVocabularyService {
    private final UserRepository userRepository;
    private final WordRepository wordRepository;
    private final UserVocabularyRepository userVocabularyRepository;

    public UserVocabularyService(UserRepository userRepository, WordRepository wordRepository, UserVocabularyRepository userVocabularyRepository) { /* Constructor */
        this.userRepository = userRepository;
        this.wordRepository = wordRepository;
        this.userVocabularyRepository = userVocabularyRepository;
    }

    @Transactional // Đảm bảo tất cả các thao tác CSDL trong phương thức này thành công hoặc thất bại cùng nhau
    public void addWordToUserList(String wordText, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tìm từ trong CSDL, nếu không có thì tạo mới và lưu lại
        Word word = wordRepository.findByWordText(wordText)
                .orElseGet(() -> wordRepository.save(new Word(wordText)));

        // Kiểm tra xem user đã lưu từ này chưa
        if (!userVocabularyRepository.existsByUser_IdAndWord_Id(user.getId(), word.getId())) {
            UserVocabulary userVocabulary = new UserVocabulary(user, word);
            userVocabularyRepository.save(userVocabulary);
        }
    }
}