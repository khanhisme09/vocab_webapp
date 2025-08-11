package com.yourname.vocabularyapp.repository;

import com.yourname.vocabularyapp.model.UserVocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserVocabularyRepository extends JpaRepository<UserVocabulary, Long> {
    // Tìm tất cả các từ đã lưu bởi một User cụ thể
    List<UserVocabulary> findByUser_Id(Long userId);

    // Kiểm tra xem một user đã lưu một từ cụ thể chưa
    boolean existsByUser_IdAndWord_Id(Long userId, Long wordId);
}