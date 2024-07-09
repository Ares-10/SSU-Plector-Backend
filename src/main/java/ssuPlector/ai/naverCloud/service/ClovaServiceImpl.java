package ssuPlector.ai.naverCloud.service;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import ssuPlector.global.exception.GlobalException;
import ssuPlector.global.response.code.GlobalErrorCode;

@Component
public class ClovaServiceImpl implements ClovaService {

    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    @Value("${naver.cloud.id}")
    String clientId;

    @Value("${naver.cloud.secret}")
    String clientSecret;

    public ClovaServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.webClient =
                WebClient.builder().baseUrl("https://naveropenapi.apigw.ntruss.com").build();
    }

    public String soundToText(File file) {

        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String language = "Kor";

            Mono<String> responseMono =
                    webClient
                            .post()
                            .uri(
                                    uriBuilder ->
                                            uriBuilder
                                                    .path("/recog/v1/stt")
                                                    .queryParam("lang", language)
                                                    .build())
                            .header("X-NCP-APIGW-API-KEY-ID", clientId)
                            .header("X-NCP-APIGW-API-KEY", clientSecret)
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .bodyValue(fileContent)
                            .retrieve()
                            .bodyToMono(String.class)
                            .map(this::getTextFromResponse);

            return responseMono.block();
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }
    }

    private String getTextFromResponse(String responseStr) {
        try {
            return objectMapper.readTree(responseStr).get("text").asText();
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }
    }

    public String summarizeText(String text) {

        try {
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> document = new HashMap<>();
            document.put("content", text);
            Map<String, Object> option = new HashMap<>();
            option.put("language", "ko");
            option.put("model", "general");
            option.put("tone", "2"); // 정중체
            option.put("summaryCount", 10); // 요약문장 수
            requestBody.put("document", document);
            requestBody.put("option", option);

            Mono<String> responseMono =
                    webClient
                            .post()
                            .uri("/text-summary/v1/summarize")
                            .header("X-NCP-APIGW-API-KEY-ID", clientId)
                            .header("X-NCP-APIGW-API-KEY", clientSecret)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(requestBody)
                            .retrieve()
                            .onStatus(
                                    status ->
                                            status.is4xxClientError() || status.is5xxServerError(),
                                    clientResponse ->
                                            clientResponse
                                                    .bodyToMono(String.class)
                                                    .flatMap(
                                                            errorBody -> {
                                                                if (errorBody.contains(
                                                                        "E100")) { // E100: 유효한 문장이
                                                                    // 부족한 경우
                                                                    return Mono.error(
                                                                            new GlobalException(
                                                                                    GlobalErrorCode
                                                                                            .INSUFFICIENT_VALID_SENTENCE));
                                                                } else if (errorBody.contains(
                                                                        "E003")) { // E003: 요청한 문장이
                                                                    // 너무 긴 경우
                                                                    return Mono.error(
                                                                            new GlobalException(
                                                                                    GlobalErrorCode
                                                                                            .TEXT_QUOTA_EXCEEDED));
                                                                } else if (errorBody.contains(
                                                                        "E001")) { // E001: 빈 문장인 경우
                                                                    return Mono.error(
                                                                            new GlobalException(
                                                                                    GlobalErrorCode
                                                                                            .EMPTY_TEXT));
                                                                } else {
                                                                    return Mono.error(
                                                                            new GlobalException(
                                                                                    GlobalErrorCode
                                                                                            ._INTERNAL_SERVER_ERROR));
                                                                }
                                                            }))
                            .bodyToMono(String.class)
                            .map(this::getSummaryFromResponse);

            return responseMono.block();
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }
    }

    private String getSummaryFromResponse(String responseStr) {
        try {
            return objectMapper.readTree(responseStr).get("summary").asText();
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }
    }
}
