package com.yourname.vocabularyapp.controller;

import com.yourname.vocabularyapp.model.User;
import com.yourname.vocabularyapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Trả về trang login.html
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        // Đặt một đối tượng User rỗng vào model để form có thể binding dữ liệu
        model.addAttribute("user", new User());
        return "register"; // Trả về trang register.html
    }

    @PostMapping("/register")
    public String handleRegistration(@ModelAttribute User user) {
        try {
            userService.registerUser(user);
            // Sau khi đăng ký thành công, chuyển hướng đến trang đăng nhập
//            return "redirect:/login";
            return "redirect:/login?registered=true";
        } catch (IllegalStateException e) {
            // Nếu có lỗi (ví dụ username đã tồn tại), quay lại trang đăng ký
            // (Trong một ứng dụng thực tế, bạn nên thêm thông báo lỗi)
            return "redirect:/register?error";
        }
    }
}