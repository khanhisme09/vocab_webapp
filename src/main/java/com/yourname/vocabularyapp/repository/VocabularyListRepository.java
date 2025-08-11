package com.yourname.vocabularyapp.repository;

import com.yourname.vocabularyapp.model.VocabularyList;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VocabularyListRepository extends JpaRepository<VocabularyList, Long> {

    // Cập nhật EntityGraph để tải cả ListWord và đối tượng Word bên trong nó
    @EntityGraph(attributePaths = {"listWords.word"})
    List<VocabularyList> findByUser_Id(Long userId);

    @EntityGraph(attributePaths = {"listWords.word"})
    Optional<VocabularyList> findWithWordsById(Long id);
}