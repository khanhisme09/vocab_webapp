package com.yourname.vocabularyapp.repository;

import com.yourname.vocabularyapp.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Long> {
    // Tìm một từ theo nội dung của nó
    Optional<Word> findByWordText(String wordText);
}