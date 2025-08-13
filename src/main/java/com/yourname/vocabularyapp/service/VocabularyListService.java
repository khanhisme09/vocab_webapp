package com.yourname.vocabularyapp.service;

import com.yourname.vocabularyapp.dto.PhoneticDto;
import com.yourname.vocabularyapp.dto.WordApiResponseDto;
import com.yourname.vocabularyapp.model.ListWord;
import com.yourname.vocabularyapp.model.User;
import com.yourname.vocabularyapp.model.VocabularyList;
import com.yourname.vocabularyapp.model.Word;
import com.yourname.vocabularyapp.repository.UserRepository;
import com.yourname.vocabularyapp.repository.VocabularyListRepository;
import com.yourname.vocabularyapp.repository.WordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class VocabularyListService {

    private final VocabularyListRepository listRepository;
    private final WordRepository wordRepository;
    private final UserRepository userRepository;

    private final DictionaryService dictionaryService;

    public VocabularyListService(VocabularyListRepository listRepository, WordRepository wordRepository, UserRepository userRepository, DictionaryService dictionaryService) {
        this.listRepository = listRepository;
        this.wordRepository = wordRepository;
        this.userRepository = userRepository;
        this.dictionaryService = dictionaryService;
    }

    /**
     * Tìm tất cả các list của một người dùng.
     * Sử dụng phương thức đã được tối ưu với @EntityGraph để tải luôn thông tin
     * về các từ liên quan, tránh lỗi LazyInitializationException.
     */
    public List<VocabularyList> findListsByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        // Giả định rằng bạn đã cập nhật @EntityGraph để tải 'listWords' thay vì 'words'
        // Trong VocabularyListRepository: @EntityGraph(attributePaths = {"listWords.word"})
        return listRepository.findByUser_Id(user.getId());
    }

    /**
     * Thêm một từ vào một list (tạo list mới nếu cần).
     * Phương thức này đã được cập nhật để tạo một Entity trung gian ListWord.
     *
     * @return
     */
    @Transactional
    public Map<String, Object> addWordToList(String wordText, Long listId, String newListName, String username) {
        // --- Bước 1: Lấy các đối tượng cần thiết ---
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        VocabularyList list;
        if (listId != null) {
            list = listRepository.findWithWordsById(listId)
                    .orElseThrow(() -> new RuntimeException("List not found"));
            if (!list.getUser().getId().equals(user.getId())) {
                throw new SecurityException("User does not have permission to access this list");
            }
        } else if (newListName != null && !newListName.isBlank()) {
            list = new VocabularyList(newListName, user);
        } else {
            throw new IllegalArgumentException("Must provide either an existing list ID or a new list name.");
        }

        // --- Bước 2: Tìm hoặc tạo đối tượng Word và làm giàu dữ liệu ---
        Word word = wordRepository.findByWordText(wordText)
                .orElseGet(() -> {
                    // Nếu từ chưa tồn tại trong CSDL, tạo mới và điền đầy đủ thông tin
                    Word newWord = new Word(wordText);

                    // Gọi API một lần duy nhất để lấy tất cả thông tin
                    WordApiResponseDto[] apiResponse = dictionaryService.lookupWord(wordText);

                    if (apiResponse != null) {
                        PhoneticDto phonetic = dictionaryService.getPrimaryPhonetic(apiResponse);
                        if (phonetic != null) {
                            newWord.setPhonetic(phonetic.getText());
                            newWord.setAudioUrl(phonetic.getAudio());
                        }
                        newWord.setPartOfSpeech(dictionaryService.getPrimaryPartOfSpeech(apiResponse));
                        newWord.setCachedDefinition(dictionaryService.getFirstDefinition(wordText));
                        newWord.setExampleSentence(dictionaryService.getFirstExample(wordText));
                        newWord.setRawJsonData(dictionaryService.getRawJson(apiResponse)); // Tùy chọn nâng cao
                    }

                    return wordRepository.save(newWord);
                });

        // (Tùy chọn) Có thể thêm logic ở đây để cập nhật thông tin cho từ đã tồn tại nếu cần

        // --- Bước 3: Kiểm tra trùng lặp và tạo liên kết ListWord ---
        boolean alreadyExists = list.getListWords().stream()
                .anyMatch(lw -> lw.getWord().getId().equals(word.getId()));

        if (!alreadyExists) {
            ListWord newListWord = new ListWord(list, word);
            list.getListWords().add(newListWord);
            listRepository.save(list);
        }
        return Map.of(
                "status", alreadyExists ? "exists" : "success",
                "word", word.getWordText(),
                "listName", list.getName()
        );
    }

    @Transactional
    public void removeWordFromList(Long listId, Long wordId, String username) {
        VocabularyList list = listRepository.findWithWordsById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        // Kiểm tra quyền sở hữu
        if (!list.getUser().getUsername().equals(username)) {
            throw new SecurityException("User does not have permission to modify this list");
        }

        // Tìm đối tượng ListWord cần xóa
        ListWord listWordToRemove = list.getListWords().stream()
                .filter(lw -> lw.getWord().getId().equals(wordId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Word not found in this list"));

        // Xóa khỏi collection. Do có orphanRemoval=true, Hibernate sẽ tự xóa trong CSDL
        list.getListWords().remove(listWordToRemove);

        // Không cần gọi save() vì đang trong transaction, nhưng gọi cũng không sao
        listRepository.save(list);
    }

    @Transactional
    public void updateListName(Long listId, String newName, String username) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("List name cannot be empty.");
        }

        VocabularyList list = listRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        // Rất quan trọng: Kiểm tra xem người dùng có phải là chủ sở hữu của list không
        if (!list.getUser().getUsername().equals(username)) {
            throw new SecurityException("User does not have permission to modify this list");
        }

        list.setName(newName);
        listRepository.save(list);
    }

    @Transactional
    public void deleteList(Long listId, String username) {
        VocabularyList list = listRepository.findById(listId)
                .orElseThrow(() -> new RuntimeException("List not found"));

        // CỰC KỲ QUAN TRỌNG: Kiểm tra quyền sở hữu trước khi xóa
        if (!list.getUser().getUsername().equals(username)) {
            throw new SecurityException("User does not have permission to delete this list");
        }

        listRepository.delete(list);
    }
}