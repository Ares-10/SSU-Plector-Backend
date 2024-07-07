package ssuPlector.ai.openAI.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ssuPlector.ai.openAI.dto.ChatGptRequest;
import ssuPlector.ai.openAI.dto.ChatGptResponse;

@Service
public class ChatGptServiceImpl implements ChatGptService {
    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Override
    public String recommendMeetingToDo(String query) {
        RestTemplate restTemplate = new RestTemplate();

        ChatGptRequest chatGptRequest = new ChatGptRequest(model, query);

        ChatGptResponse response =
                restTemplate.postForObject(
                        apiUrl, getHttpEntity(chatGptRequest), ChatGptResponse.class);

        assert response != null;
        return response.getChoices().get(0).getMessage().getContent();
    }

    private HttpEntity<ChatGptRequest> getHttpEntity(ChatGptRequest chatRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + apiKey);

        return new HttpEntity<>(chatRequest, headers);
    }
}
