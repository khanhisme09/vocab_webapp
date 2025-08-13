package com.yourname.vocabularyapp.dto;

import lombok.Data;

@Data
public class PasswordChangeDto {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}