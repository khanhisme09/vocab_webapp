package com.yourname.vocabularyapp.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vocabulary_lists")
@Getter @Setter @NoArgsConstructor
@ToString(exclude = {"user", "listWords"})
@EqualsAndHashCode(exclude = {"user", "listWords"})
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

    // THAY ĐỔI Ở ĐÂY: Xóa @ManyToMany, thêm @OneToMany
    @OneToMany(mappedBy = "vocabularyList", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ListWord> listWords = new HashSet<>();

    public VocabularyList(String name, User user) {
        this.name = name;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
}