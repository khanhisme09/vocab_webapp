package com.yourname.vocabularyapp.service;

import com.yourname.vocabularyapp.dto.PasswordChangeDto;
import com.yourname.vocabularyapp.model.User;
import com.yourname.vocabularyapp.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Phương thức này được Spring Security gọi khi một người dùng cố gắng đăng nhập
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Trả về một đối tượng UserDetails mà Spring Security có thể hiểu
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("USER") // Gán quyền cho người dùng
                .build();
    }

    public void registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already exists!");
        }
        // Quan trọng: Mã hóa mật khẩu trước khi lưu vào CSDL
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void changePassword(String username, PasswordChangeDto passwordChangeDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 1. Kiểm tra mật khẩu cũ có khớp không
        if (!passwordEncoder.matches(passwordChangeDto.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect old password.");
        }

        // 2. Kiểm tra mật khẩu mới và mật khẩu xác nhận có giống nhau không
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match.");
        }

        // 3. (Tùy chọn) Thêm các quy tắc cho mật khẩu mới (ví dụ: độ dài tối thiểu)
        if (passwordChangeDto.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long.");
        }

        // 4. Mã hóa và lưu mật khẩu mới
        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        userRepository.save(user);
    }
}