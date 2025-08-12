package com.yourname.vocabularyapp.service;

import com.yourname.vocabularyapp.dto.DashboardStatsDto;
import com.yourname.vocabularyapp.model.VocabularyList;
import com.yourname.vocabularyapp.model.Word;
import com.yourname.vocabularyapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        // listService.findListsByUsername đã được tối ưu để tải các dữ liệu cần thiết
        List<VocabularyList> lists = listService.findListsByUsername(username);

        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalLists(lists.size());

        // THAY ĐỔI LỚN: Cách tính tổng số từ duy nhất và số từ mỗi list

        // Tính tổng số từ duy nhất trên tất cả các list
        Set<Word> uniqueWords = new HashSet<>();
        lists.forEach(list ->
                list.getListWords().forEach(listWord -> uniqueWords.add(listWord.getWord()))
        );
        stats.setTotalUniqueWords(uniqueWords.size());

        // Tạo map chứa số từ của mỗi list
        stats.setWordsPerList(lists.stream()
                .collect(Collectors.toMap(
                        VocabularyList::getName,
                        list -> list.getListWords().size() // Giờ ta lấy size của listWords
                ))
        );

        return stats;
    }
}