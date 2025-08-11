package com.yourname.vocabularyapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "list_words")
@Getter @Setter @NoArgsConstructor
public class ListWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id")
    private VocabularyList vocabularyList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    // --- CÁC TRƯỜNG MỚI CHO SRS ---

    // Mức độ thành thạo, ví dụ: 0 (mới học), 1, 2, 3...
    @Column(name = "srs_level", nullable = false, columnDefinition = "int default 0")
    private Integer srsLevel = 0;

    // Ngày cần ôn tập tiếp theo
    @Column(name = "next_review_date")
    private LocalDateTime nextReviewDate;

    @Column(name = "added_date")
    private LocalDateTime addedDate;

    public ListWord(VocabularyList list, Word word) {
        this.vocabularyList = list;
        this.word = word;
        this.addedDate = LocalDateTime.now();
        // Khi từ mới được thêm, cần ôn tập ngay lập tức
        this.nextReviewDate = LocalDateTime.now();
    }
}