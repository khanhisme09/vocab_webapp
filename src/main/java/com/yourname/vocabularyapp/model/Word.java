package com.yourname.vocabularyapp.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    @Column(name = "phonetic")
    private String phonetic;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "part_of_speech")
    private String partOfSpeech; // Loại từ chính

    @Column(name = "cached_definition", columnDefinition = "TEXT")
    private String cachedDefinition;

    @Column(name = "example_sentence", columnDefinition = "TEXT")
    private String exampleSentence;

    // TÙY CHỌN NÂNG CAO: Lưu toàn bộ JSON gốc
    @Column(name = "raw_json_data", columnDefinition = "JSON")
    private String rawJsonData;


    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ListWord> listWords;

    public Word(String wordText) {
        this.wordText = wordText;
    }
}