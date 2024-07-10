package ssuPlector.ai.openAI.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ssuPlector.ai.openAI.dto.ChatGptRequest;
import ssuPlector.ai.openAI.dto.ChatGptResponse;
import ssuPlector.global.exception.GlobalException;
import ssuPlector.global.response.code.GlobalErrorCode;

@Service
public class ChatGptServiceImpl implements ChatGptService {
    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Autowired private RestTemplate restTemplate;

    @Override
    public String recommendMeetingToDo(String query) {
        RestTemplate restTemplate = new RestTemplate();

        ChatGptRequest chatGptRequest = new ChatGptRequest(model, query);

        try {
            ChatGptResponse response =
                    restTemplate.postForObject(
                            apiUrl, getHttpEntity(chatGptRequest), ChatGptResponse.class);
            return response.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }
    }

    private HttpEntity<ChatGptRequest> getHttpEntity(ChatGptRequest chatRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + apiKey);

        return new HttpEntity<>(chatRequest, headers);
    }

    public String summarizeText(String text) {
        text =
                "다음 텍스트를 프로젝트 회의록 형식에 맞게 요약해서 정리한 결과물을 반환해주세요. "
                        + "프로젝트 회의록 형식은 다음과 같아야 합니다:\n"
                        + "1. 회의 제목: [제목]\n"
                        + "2. 회의 날짜: [날짜]\n"
                        + "3. 참석자: [참석자 목록]\n"
                        + "4. 회의 목적: [목적]\n"
                        + "5. 진행 상황 공유: [진행 상황]\n"
                        + "6. 이슈 및 리스크: [이슈 및 리스크]\n"
                        + "7. 주요 논의 사항: [논의된 주요 사항들]\n"
                        + "8. 결론: [결론]\n"
                        + "9. 다음 단계: [향후 계획 및 액션 아이템]\n\n"
                        + text;

        ChatGptRequest request = new ChatGptRequest(model, text);
        ChatGptResponse response =
                restTemplate.postForObject(apiUrl, request, ChatGptResponse.class);

        if (response == null) {
            System.out.println("response is null");
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }

        return response.getChoices().get(0).getMessage().getContent();
    }
}
