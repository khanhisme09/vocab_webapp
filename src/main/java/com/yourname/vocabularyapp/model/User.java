package com.yourname.vocabularyapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users") // Tên của bảng trong CSDL
@Data // Lombok: tự tạo getters, setters, equals, hashCode, toString
@NoArgsConstructor // Lombok: tự tạo constructor không tham số
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID tự tăng
    private Long id;

    @Column(nullable = false, unique = true) // Không được null, không được trùng
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // Mật khẩu này sẽ được mã hóa
}