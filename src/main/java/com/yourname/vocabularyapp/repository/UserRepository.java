package com.yourname.vocabularyapp.repository;

import com.yourname.vocabularyapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA tự hiểu phương thức này và tạo câu truy vấn
    // để tìm User theo username. Dùng cho việc đăng nhập và kiểm tra trùng lặp.
    Optional<User> findByUsername(String username);
}