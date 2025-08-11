package com.yourname.vocabularyapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "words")
@Data
@NoArgsConstructor
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Đảm bảo không có 2 từ giống hệt nhau trong bảng
    @Column(nullable = false, unique = true)
    private String wordText;

    public Word(String wordText) {
        this.wordText = wordText;
    }
}