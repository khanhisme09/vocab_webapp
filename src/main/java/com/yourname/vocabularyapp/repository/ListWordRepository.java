package com.yourname.vocabularyapp.repository;

import com.yourname.vocabularyapp.model.ListWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ListWordRepository extends JpaRepository<ListWord, Long> {
}
