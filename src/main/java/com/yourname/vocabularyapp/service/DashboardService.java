package com.yourname.vocabularyapp.service;

import com.yourname.vocabularyapp.dto.DashboardStatsDto;
import com.yourname.vocabularyapp.model.User;
import com.yourname.vocabularyapp.model.VocabularyList;
import com.yourname.vocabularyapp.model.Word;
import com.yourname.vocabularyapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    // Inject VocabularyListService thay vì Repository để tận dụng các phương thức đã có
    private final VocabularyListService listService;

    public DashboardService(UserRepository userRepository, VocabularyListService listService) {
        this.userRepository = userRepository;
        this.listService = listService;
    }

    public DashboardStatsDto getStatsForUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        // Sử dụng phương thức đã được tối ưu với @EntityGraph
        List<VocabularyList> lists = listService.findListsByUsername(username);

        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalLists(lists.size());

        // Tính tổng số từ duy nhất trên tất cả các list
        Set<Word> uniqueWords = new HashSet<>();
        lists.forEach(list -> uniqueWords.addAll(list.getWords()));
        stats.setTotalUniqueWords(uniqueWords.size());

        // Tạo map chứa số từ của mỗi list
        stats.setWordsPerList(lists.stream()
                .collect(Collectors.toMap(
                        VocabularyList::getName,
                        list -> list.getWords().size()
                ))
        );

        return stats;
    }
}