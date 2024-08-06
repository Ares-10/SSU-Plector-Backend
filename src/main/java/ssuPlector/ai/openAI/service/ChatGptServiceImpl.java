package ssuPlector.ai.openAI.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ssuPlector.ai.openAI.dto.ChatGptRequest.ChatGptCommentRequest;
import ssuPlector.ai.openAI.dto.ChatGptRequest.ChatGptImageRequest;
import ssuPlector.ai.openAI.dto.ChatGptResponse.ChatGptCommentResponse;
import ssuPlector.ai.openAI.dto.ChatGptResponse.ChatGptImageResponse;
import ssuPlector.ai.openAI.dto.Message;
import ssuPlector.global.exception.GlobalException;
import ssuPlector.global.response.code.GlobalErrorCode;

@Service
public class ChatGptServiceImpl implements ChatGptService {
    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.image-url}")
    private String imageUrl;

    @Autowired private RestTemplate restTemplate;

    @Override
    public String standardChat(String query) {
        return standardChat(query, model);
    }

    @Override
    public String standardChat(String query, String model) {
        ChatGptCommentRequest request = new ChatGptCommentRequest(model, query);
        ChatGptCommentResponse response;
        try {
            response = restTemplate.postForObject(apiUrl, request, ChatGptCommentResponse.class);
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }
        return response.getChoices().get(0).getMessage().getContent();
    }

    @Override
    public String makeImage(String imageInfo) {
        List<Message> messages = new ArrayList<>();
        messages.add(
                new Message(
                        "system",
                        "We're going to use open ai's image generation API, "
                                + "so we need to create a prompt in English to generate an image based on the text we enter. "
                                + "Only give me the prompt."));
        messages.add(new Message("user", imageInfo));

        String imageGenerateQuery;
        try {
            ChatGptCommentRequest request = new ChatGptCommentRequest("gpt-4o", messages);
            ChatGptCommentResponse response =
                    restTemplate.postForObject(apiUrl, request, ChatGptCommentResponse.class);
            imageGenerateQuery = response.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }

        System.out.println(imageGenerateQuery);

        try {
            ChatGptImageRequest request = new ChatGptImageRequest(imageGenerateQuery, 1, "512x512");
            ChatGptImageResponse response =
                    restTemplate.postForObject(imageUrl, request, ChatGptImageResponse.class);
            return response.getData().get(0).getUrl();
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String branding(String projectInfo) {
        List<Message> messages = new ArrayList<>();
        messages.add(
                new Message(
                        "system",
                        "You are an assistant who creates (recommends) branding in a specific format. "
                                + "You take input from ideas, topics, descriptions, etc. "
                                + "The format of the branding you write looks like this: \n"
                                + "<기본 요소>\n"
                                + "1. 색상: [색상]\n"
                                + "2. 폰트: [폰트]\n"
                                + "3. 텍스처: [텍스처]\n"
                                + "4. 슬로건: [슬로건]\n"
                                + "<핵심 전략>\n"
                                + "1. 대상 고객 이해하기: [맞춤 설명]\n"
                                + "2. 앱 브랜딩을 위한 브랜드 아이덴티티 정의하기: [맞춤 설명]\n"
                                + "3. 포괄적인 마케팅 계획 세우기: [맞춤 설명]\n"
                                + "4. 사용자 경험에 집중하기: [맞춤 설명]"));
        messages.add(new Message("user", projectInfo));
        ChatGptCommentRequest request = new ChatGptCommentRequest("gpt-4o", messages);
        ChatGptCommentResponse response;
        try {
            response = restTemplate.postForObject(apiUrl, request, ChatGptCommentResponse.class);
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }
        return response.getChoices().get(0).getMessage().getContent();
    }
}
