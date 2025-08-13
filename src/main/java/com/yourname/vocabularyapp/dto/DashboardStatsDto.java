package com.yourname.vocabularyapp.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardStatsDto {
    // --- Các trường cũ ---
    private long totalLists;
    private long totalUniqueWords;
    private Map<String, Integer> wordsPerList;

    // --- CÁC TRƯỜNG MỚI ---

    // Mục tiêu 1: Thống kê SRS
    private long wordsToReviewToday; // Số từ cần ôn tập hôm nay
    private Map<String, Long> wordsBySrsLevel; // Phân loại từ theo level

    // Mục tiêu 2: Lịch sử hoạt động
    private List<Map<String, Object>> wordsLearnedLast7Days; // Thống kê 7 ngày qua
    private List<String> recentlyAddedWords; // 5 từ mới nhất
}