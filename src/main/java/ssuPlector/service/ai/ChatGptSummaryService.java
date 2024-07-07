package ssuPlector.service.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ssuPlector.global.exception.GlobalException;
import ssuPlector.global.response.code.GlobalErrorCode;
import ssuPlector.service.ai.chatgpt.ChatGptRequest;
import ssuPlector.service.ai.chatgpt.ChatGptResponse;

@Component
public class ChatGptSummaryService {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Autowired private RestTemplate restTemplate;

    public String summarize(String text) {
        text = "다음 텍스트를 회의록 형식에 맞게 요약해서 정리한 결과물을 반환해주세요.\n" + text;

        ChatGptRequest request = new ChatGptRequest(model, text);
        ChatGptResponse response =
                restTemplate.postForObject(apiUrl, request, ChatGptResponse.class);

        if (response == null) {
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }

        return response.getChoices().get(0).getMessage().getContent();
    }
}
