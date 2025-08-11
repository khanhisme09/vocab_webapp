package com.yourname.vocabularyapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_vocabulary")
@Data
@NoArgsConstructor
public class UserVocabulary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mối quan hệ nhiều-một: nhiều bản ghi UserVocabulary thuộc về một User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Mối quan hệ nhiều-một: nhiều bản ghi UserVocabulary thuộc về một Word
    @ManyToOne
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Column(name = "added_date")
    private LocalDateTime addedDate;

    public UserVocabulary(User user, Word word) {
        this.user = user;
        this.word = word;
        this.addedDate = LocalDateTime.now();
    }
}