package com.yourname.vocabularyapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data // Lombok tự động tạo getter, setter, toString()...
@JsonIgnoreProperties(ignoreUnknown = true) // Bỏ qua các trường không cần thiết trong JSON
public class DefinitionDto {
    private String definition;
    private String example;
}