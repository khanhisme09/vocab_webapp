package com.yourname.vocabularyapp.service;

import com.yourname.vocabularyapp.dto.MeaningDto;
import com.yourname.vocabularyapp.dto.WordApiResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class DictionaryService {

    private static final String DICTIONARY_API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

    // RestTemplate là một công cụ mạnh mẽ của Spring để thực hiện các cuộc gọi HTTP
    private final RestTemplate restTemplate = new RestTemplate();

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

}