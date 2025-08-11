package com.yourname.vocabularyapp.model;

import jakarta.persistence.*;
// import lombok.Data; // <-- XÓA HOẶC COMMENT DÒNG NÀY
import lombok.Getter;   // <-- THÊM CÁC IMPORT NÀY
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "vocabularyLists")
@EqualsAndHashCode(exclude = "vocabularyLists")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VocabularyList> vocabularyLists;
}