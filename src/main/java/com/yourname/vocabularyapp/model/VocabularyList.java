package com.yourname.vocabularyapp.model;

import jakarta.persistence.*;
// import lombok.Data; // <-- XÓA HOẶC COMMENT DÒNG NÀY
import lombok.Getter;   // <-- THÊM CÁC IMPORT NÀY
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vocabulary_lists")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"user", "words"})
@EqualsAndHashCode(exclude = {"user", "words"})
public class VocabularyList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "list_words",
            joinColumns = @JoinColumn(name = "list_id"),
            inverseJoinColumns = @JoinColumn(name = "word_id")
    )
    private Set<Word> words = new HashSet<>();

    public VocabularyList(String name, User user) {
        this.name = name;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
}