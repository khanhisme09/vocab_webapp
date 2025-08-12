package com.yourname.vocabularyapp.repository;

import com.yourname.vocabularyapp.model.ListWord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListWordRepository extends JpaRepository<ListWord, Long> {

}
