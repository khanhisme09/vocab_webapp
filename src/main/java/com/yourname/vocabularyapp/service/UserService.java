package com.yourname.vocabularyapp.service;

import com.yourname.vocabularyapp.model.User;
import com.yourname.vocabularyapp.repository.UserRepository;
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
}