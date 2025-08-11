package com.yourname.vocabularyapp.dto;

import lombok.Data;
import java.util.Map;

@Data
public class DashboardStatsDto {
    private long totalLists;
    private long totalUniqueWords;
    private Map<String, Integer> wordsPerList; // Map<Tên List, Số từ>
}