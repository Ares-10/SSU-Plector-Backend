package ssuPlector.service.ai;

import java.io.File;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import ssuPlector.global.exception.GlobalException;
import ssuPlector.global.response.code.GlobalErrorCode;

@Component
public class NaverClovaService {

    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    @Value("${naver.cloud.id}")
    String clientId;

    @Value("${naver.cloud.secret}")
    String clientSecret;

    public NaverClovaService(ObjectMapper objectMapper) {
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
}
