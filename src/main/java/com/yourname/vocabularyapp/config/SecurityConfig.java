package com.yourname.vocabularyapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean // Tạo một Bean PasswordEncoder để mã hóa mật khẩu
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Cho phép tất cả mọi người truy cập chức năng logout
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Cho phép tất cả mọi người truy cập các URL này
                        .requestMatchers("/", "/search", "/register", "/css/**", "/js/**").permitAll()
                        // Tất cả các request còn lại đều cần phải được xác thực (đăng nhập)
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // Sử dụng trang đăng nhập tùy chỉnh tại URL /login
                        .loginPage("/login")
                        // Xử lý submit form đăng nhập tại URL /login
                        .loginProcessingUrl("/login")
                        // Chuyển hướng đến trang chủ sau khi đăng nhập thành công
                        .defaultSuccessUrl("/", true)
                        // Cho phép tất cả mọi người truy cập trang đăng nhập
                        .permitAll()
                )
                .logout(LogoutConfigurer::permitAll
                );

        return http.build();
    }
}