package ssuPlector.ai.openAI.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ssuPlector.ai.openAI.dto.ChatGptRequest.ChatGptCommentRequest;
import ssuPlector.ai.openAI.dto.ChatGptRequest.ChatGptImageRequest;
import ssuPlector.ai.openAI.dto.ChatGptResponse.ChatGptCommentResponse;
import ssuPlector.ai.openAI.dto.ChatGptResponse.ChatGptImageResponse;
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

    @Value("${openai.api.image-url}")
    private String imageUrl;

    @Autowired private RestTemplate restTemplate;

    @Override
    public String recommendMeetingToDo(String query) {
        RestTemplate restTemplate = new RestTemplate();

        ChatGptCommentRequest chatGptCommentRequest = new ChatGptCommentRequest(model, query);

        try {
            ChatGptCommentResponse response =
                    restTemplate.postForObject(
                            apiUrl,
                            getHttpEntity(chatGptCommentRequest),
                            ChatGptCommentResponse.class);
            return response.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }
    }

    private HttpEntity<ChatGptCommentRequest> getHttpEntity(ChatGptCommentRequest chatRequest) {
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
        return standardChat(text);
    }

    @Override
    public String standardChat(String query) {
        return standardChat(query, model);
    }

    @Override
    public String standardChat(String query, String model) {
        ChatGptCommentRequest request = new ChatGptCommentRequest(model, query);
        ChatGptCommentResponse response = null;
        try {
            response = restTemplate.postForObject(apiUrl, request, ChatGptCommentResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }
        return response.getChoices().get(0).getMessage().getContent();
    }

    @Override
    public String makeImage(String query) {
        ChatGptImageRequest request = new ChatGptImageRequest(query, 1, "256x256");
        ChatGptImageResponse response = null;
        try {
            response = restTemplate.postForObject(imageUrl, request, ChatGptImageResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }
        return response.getData().get(0).getUrl();
    }
}
