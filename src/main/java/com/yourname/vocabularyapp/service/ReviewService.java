package com.yourname.vocabularyapp.service;

import com.yourname.vocabularyapp.model.ListWord;
import com.yourname.vocabularyapp.repository.ListWordRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class ReviewService {

    private final ListWordRepository listWordRepository;
    // Các khoảng thời gian ôn tập (theo giờ) cho mỗi level
    // 4h, 8h, 1d, 3d, 7d, 14d, 30d, 60d
    private static final long[] SRS_INTERVALS = {4, 8, 24, 72, 168, 336, 720, 1440};

    public ReviewService(ListWordRepository listWordRepository) {
        this.listWordRepository = listWordRepository;
    }

    public void processAnswer(Long listWordId, boolean isCorrect) {
        ListWord listWord = listWordRepository.findById(listWordId)
                .orElseThrow(() -> new RuntimeException("ListWord with id " + listWordId + " not found"));

        if (isCorrect) {
            // Tăng level và tính ngày ôn tập tiếp theo
            int currentLevel = listWord.getSrsLevel();
            int nextLevel = Math.min(currentLevel + 1, SRS_INTERVALS.length - 1);
            listWord.setSrsLevel(nextLevel);
            long hoursToAdd = SRS_INTERVALS[nextLevel];
            listWord.setNextReviewDate(LocalDateTime.now().plus(hoursToAdd, ChronoUnit.HOURS));
        } else {
            // Nếu sai, reset level về mức thấp hơn
            int currentLevel = listWord.getSrsLevel();
            int nextLevel = Math.max(0, currentLevel - 2); // Giảm 2 bậc nhưng không xuống dưới 0
            listWord.setSrsLevel(nextLevel);
            long hoursToAdd = SRS_INTERVALS[nextLevel];
            listWord.setNextReviewDate(LocalDateTime.now().plus(hoursToAdd, ChronoUnit.HOURS));
        }
        listWordRepository.save(listWord);
    }
}