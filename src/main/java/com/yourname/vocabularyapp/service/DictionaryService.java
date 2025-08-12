package com.yourname.vocabularyapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourname.vocabularyapp.dto.PhoneticDto;
import com.yourname.vocabularyapp.dto.WordApiResponseDto;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class DictionaryService {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(DictionaryService.class);
    private static final String DICTIONARY_API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

    // RestTemplate là một công cụ mạnh mẽ của Spring để thực hiện các cuộc gọi HTTP
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WordApiResponseDto[] lookupWord(String word) {
        try {
            // Tạo URL đầy đủ, ví dụ: https://api.dictionaryapi.dev/api/v2/entries/en/hello
            String url = DICTIONARY_API_URL + word;

            // Gọi API và tự động chuyển đổi kết quả JSON thành mảng các đối tượng DTO của chúng ta
            return restTemplate.getForObject(url, WordApiResponseDto[].class);
        } catch (HttpClientErrorException.NotFound e) {
            // Xử lý trường hợp API trả về lỗi 404 (Không tìm thấy từ)
            System.out.println("Word not found: " + word);
            return null;
        } catch (Exception e) {
            // Xử lý các lỗi khác
            System.err.println("An error occurred while calling the dictionary API: " + e.getMessage());
            return null;
        }
    }

    public String getFirstDefinition(String word) {
        WordApiResponseDto[] response = lookupWord(word);
        if (response != null && response.length > 0) {
            return response[0].getMeanings().get(0).getDefinitions().get(0).getDefinition();
        }
        return null;
    }
    // Trả về câu ví dụ đầu tiên tìm thấy của một từ
    public String getFirstExample(String word) {
        WordApiResponseDto[] response = lookupWord(word);
        if (response != null && response.length > 0) {
            // Duyệt qua các nghĩa và định nghĩa để tìm câu ví dụ đầu tiên có sẵn
            for (var meaning : response[0].getMeanings()) {
                for (var definition : meaning.getDefinitions()) {
                    if (definition.getExample() != null && !definition.getExample().isBlank()) {
                        return definition.getExample();
                    }
                }
            }
        }
        return null; // Trả về null nếu không tìm thấy ví dụ nào
    }

    public PhoneticDto getPrimaryPhonetic(WordApiResponseDto[] response) {
        if (response == null || response.length == 0) return null;
        for (var phonetic : response[0].getPhonetics()) {
            if (phonetic.getText() != null && !phonetic.getText().isBlank() &&
                    phonetic.getAudio() != null && !phonetic.getAudio().isBlank()) {
                return phonetic;
            }
        }
        // Nếu không có cái nào hoàn hảo, lấy cái đầu tiên có text
        if (!response[0].getPhonetics().isEmpty()) {
            return response[0].getPhonetics().get(0);
        }
        return null;
    }

    // Lấy loại từ đầu tiên
    public String getPrimaryPartOfSpeech(WordApiResponseDto[] response) {
        if (response != null && response.length > 0 && !response[0].getMeanings().isEmpty()) {
            return response[0].getMeanings().get(0).getPartOfSpeech();
        }
        return null;
    }

    // Chuyển đối tượng DTO thành chuỗi JSON
    public String getRawJson(WordApiResponseDto[] response) {
        if (response == null || response.length == 0) return null;
        try {
            return objectMapper.writeValueAsString(response[0]);
        } catch (Exception e) {
            logger.error("Error converting DTO to JSON", e);
            return null;
        }
    }

}