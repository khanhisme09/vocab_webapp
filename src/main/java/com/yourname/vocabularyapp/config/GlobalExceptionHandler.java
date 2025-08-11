package com.yourname.vocabularyapp.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

// Annotation này cho phép class này xử lý các exception từ tất cả các Controller khác
@ControllerAdvice
public class GlobalExceptionHandler {

    // Xử lý tất cả các lỗi chung (ví dụ: RuntimeException, lỗi 500)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllExceptions(Exception ex) {
        ModelAndView modelAndView = new ModelAndView("error"); // Trỏ đến trang error.html
        modelAndView.addObject("errorMessage", "An unexpected error occurred. Please try again later.");
        modelAndView.addObject("errorDetails", ex.getMessage()); // Chỉ dùng để debug, có thể xóa khi deploy
        return modelAndView;
    }
}