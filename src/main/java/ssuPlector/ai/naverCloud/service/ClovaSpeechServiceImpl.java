package ssuPlector.ai.naverCloud.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import ssuPlector.global.exception.GlobalException;
import ssuPlector.global.response.code.GlobalErrorCode;

@Component
public class ClovaSpeechServiceImpl implements ClovaSpeechService {

    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    @Value("${naver.cloud.clovaSpeech.client-secret}")
    String clientSecret;

    public ClovaSpeechServiceImpl(
            ObjectMapper objectMapper,
            @Value("${naver.cloud.clovaSpeech.invoke-url}") String invokeUrl) {
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder().baseUrl(invokeUrl).build();
    }

    @Override
    public String convert(File file) {

        try {
            String language = "ko-KR";

            Mono<String> responseMono =
                    webClient
                            .post()
                            .uri("/recognizer/upload")
                            .header("X-CLOVASPEECH-API-KEY", clientSecret)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .bodyValue(generateMultipartBody(file, language))
                            .retrieve()
                            .bodyToMono(String.class)
                            .map(this::getTextFromResponse);

            return responseMono.block();
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }
    }

    private MultiValueMap<String, Object> generateMultipartBody(File file, String language) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("media", new FileSystemResource(file));

        Map<String, Object> params = new HashMap<>();
        params.put("language", language);
        params.put("completion", "sync");
        params.put("wordAlignment", true);
        params.put("fullText", true);
        params.put("noiseFiltering", true);
        params.put("diarization", Map.of("enable", true));
        params.put("format", "JSON");

        body.add("params", new HttpEntity<>(params, createJsonHeaders()));
        return body;
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String getTextFromResponse(String responseStr) {
        try {
            return objectMapper.readTree(responseStr).get("text").asText();
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }
    }
}
