package ssuPlector.ai.openAI.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.url}")
    private String imageUrl;

    @Autowired private RestTemplate restTemplate;

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
