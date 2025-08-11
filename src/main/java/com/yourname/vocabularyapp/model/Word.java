package com.yourname.vocabularyapp.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "words")
@Getter @Setter @NoArgsConstructor
@ToString(exclude = "listWords")
@EqualsAndHashCode(exclude = "listWords")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String wordText;

    // THAY ĐỔI Ở ĐÂY: Xóa @ManyToMany, thêm @OneToMany
    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ListWord> listWords = new HashSet<>();

    public Word(String wordText) {
        this.wordText = wordText;
    }
}