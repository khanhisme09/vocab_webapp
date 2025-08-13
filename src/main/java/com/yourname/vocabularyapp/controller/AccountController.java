package com.yourname.vocabularyapp.controller;

import com.yourname.vocabularyapp.dto.DashboardStatsDto;
import com.yourname.vocabularyapp.dto.PasswordChangeDto;
import com.yourname.vocabularyapp.model.User;
import com.yourname.vocabularyapp.repository.UserRepository;
import com.yourname.vocabularyapp.service.DashboardService;
import com.yourname.vocabularyapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class AccountController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final DashboardService dashboardService;

    public AccountController(UserService userService, UserRepository userRepository, DashboardService dashboardService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/account")
    public String accountPage(Model model, Principal principal) {
        String username = principal.getName();

        // Lấy thông tin người dùng
        User currentUser = userRepository.findByUsername(username).orElseThrow();
        model.addAttribute("user", currentUser);

        // LẤY THÔNG TIN THỐNG KÊ
        DashboardStatsDto stats = dashboardService.getStatsForUser(username);
        model.addAttribute("stats", stats);

        // Đặt DTO rỗng cho form
        model.addAttribute("passwordChangeDto", new PasswordChangeDto());

        return "account";
    }

    @PostMapping("/account/change-password")
    public String changePassword(@ModelAttribute PasswordChangeDto passwordChangeDto,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.changePassword(principal.getName(), passwordChangeDto);
            redirectAttributes.addFlashAttribute("toast_success", "Password changed successfully!");
        } catch (Exception e) {
            // Gửi thông báo lỗi cụ thể về lại trang
            redirectAttributes.addFlashAttribute("toast_error", e.getMessage());
        }
        return "redirect:/account";
    }
}