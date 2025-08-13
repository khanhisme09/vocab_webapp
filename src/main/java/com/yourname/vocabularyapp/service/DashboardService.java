package com.yourname.vocabularyapp.service;

import com.yourname.vocabularyapp.dto.DashboardStatsDto;
import com.yourname.vocabularyapp.model.ListWord;
import com.yourname.vocabularyapp.model.User;
import com.yourname.vocabularyapp.model.VocabularyList;
import com.yourname.vocabularyapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final VocabularyListService listService;

    public DashboardService(UserRepository userRepository, VocabularyListService listService) {
        this.userRepository = userRepository;
        this.listService = listService;
    }

    public DashboardStatsDto getStatsForUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        // Tải tất cả các list và các từ liên quan một lần duy nhất
        List<VocabularyList> lists = listService.findListsByUsername(username);

        // Gom tất cả các bản ghi ListWord từ tất cả các list vào một danh sách duy nhất
        List<ListWord> allListWords = lists.stream()
                .flatMap(list -> list.getListWords().stream())
                .collect(Collectors.toList());

        DashboardStatsDto stats = new DashboardStatsDto();

        // --- Các thống kê cũ ---
        stats.setTotalLists(lists.size());
        stats.setTotalUniqueWords(allListWords.stream().map(ListWord::getWord).distinct().count());
        stats.setWordsPerList(lists.stream()
                .collect(Collectors.toMap(VocabularyList::getName, list -> list.getListWords().size())));

        // --- THỐNG KÊ MỚI ---

        // 1. Thống kê SRS
        LocalDateTime now = LocalDateTime.now();
        stats.setWordsToReviewToday(allListWords.stream()
                .filter(lw -> lw.getNextReviewDate() != null && !lw.getNextReviewDate().isAfter(now))
                .count());

        stats.setWordsBySrsLevel(allListWords.stream()
                .collect(Collectors.groupingBy(this::categorizeSrsLevel, Collectors.counting())));

        // 2. Lịch sử hoạt động
        stats.setRecentlyAddedWords(allListWords.stream()
                .sorted(Comparator.comparing(ListWord::getAddedDate).reversed())
                .limit(5)
                .map(lw -> lw.getWord().getWordText())
                .collect(Collectors.toList()));

        stats.setWordsLearnedLast7Days(calculateWordsLearnedLast7Days(allListWords));

        return stats;
    }

    // Hàm helper để phân loại level SRS
    private String categorizeSrsLevel(ListWord listWord) {
        int level = listWord.getSrsLevel();
        if (level == 0) return "New";
        if (level >= 1 && level <= 3) return "Learning";
        if (level >= 4 && level <= 6) return "Reviewing";
        return "Mastered";
    }

    // Hàm helper để tính số từ học trong 7 ngày qua
    private List<Map<String, Object>> calculateWordsLearnedLast7Days(List<ListWord> allListWords) {
        LocalDate today = LocalDate.now();
        Map<LocalDate, Long> wordsByDate = allListWords.stream()
                .filter(lw -> lw.getAddedDate().toLocalDate().isAfter(today.minusDays(7)))
                .collect(Collectors.groupingBy(lw -> lw.getAddedDate().toLocalDate(), Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long count = wordsByDate.getOrDefault(date, 0L);
            Map<String, Object> dayStat = new HashMap<>();
            dayStat.put("date", date.toString());
            dayStat.put("count", count);
            result.add(dayStat);
        }
        return result;
    }
}