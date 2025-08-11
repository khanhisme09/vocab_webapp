package com.yourname.vocabularyapp.repository;

import com.yourname.vocabularyapp.model.VocabularyList;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VocabularyListRepository extends JpaRepository<VocabularyList, Long> {
    @EntityGraph(attributePaths = {"words"})
    List<VocabularyList> findByUser_Id(Long userId);

    @EntityGraph(attributePaths = {"words"})
    Optional<VocabularyList> findWithWordsById(Long id);
}