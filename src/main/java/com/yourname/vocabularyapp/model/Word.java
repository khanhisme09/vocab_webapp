package com.yourname.vocabularyapp.model;

import jakarta.persistence.*;
// import lombok.Data; // <-- XÓA HOẶC COMMENT DÒNG NÀY
import lombok.Getter;   // <-- THÊM CÁC IMPORT NÀY
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import lombok.NoArgsConstructor;
import java.util.Set;

@Entity
@Table(name = "words")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "vocabularyLists")
@EqualsAndHashCode(exclude = "vocabularyLists")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Đảm bảo không có 2 từ giống hệt nhau trong bảng
    @Column(nullable = false, unique = true)
    private String wordText;

    @ManyToMany(mappedBy = "words")
    private Set<VocabularyList> vocabularyLists;

    public Word(String wordText) {
        this.wordText = wordText;
    }
}