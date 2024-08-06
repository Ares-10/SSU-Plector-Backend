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
                        "너는 이미지 생성을 위한 프롬프트 생성 어시스턴트야. 응답으로 오로지 프롬프트만 생성해줘"
                                + "너가 생성해야 할 프롬프트의 종류는 2가지야. <로고(아이콘), 마스코드(케릭터)>. "
                                + "다음과 같은 형식으로 프롬프트를 만들어줘. 주어진 형식의 대괄호는 너가 선택 또는 수정 가능한 부분이야. "
                                + "\n'로고(아이콘)' 프롬프트 형식.\n"
                                + "1. 참조 아이콘 스타일: 해당 분야와 관련된 앱스토어 인기순위 상위 100개 기업의 로고를 참조\n"
                                + "2. 아이콘 설명:\n"
                                + "    - 설명: [설명]\n"
                                + "    - 모양: 테두리가 둥근 정사각형\n"
                                + "    - 색깔: [설명]\n"
                                + "3. 배경\n"
                                + "    - 설명: 아무것도 없는 흰색 바탕\n"
                                + "4. 이미지 차원\n"
                                + "    - 차원: [2D/3D]\n"
                                + "\n'마스코드(케릭터)' 프롬프트 형식\n"
                                + "1. 참조 애니메이션 스타일: 동종 업계의 상위 100개 기업의 마스코트를 참조\n"
                                + "2. 참조 형태:\n"
                                + "    - 종류: [인간/동물/식물/로봇/사물]\n"
                                + "3. 등장인물 숫자, 성별, 외형 설명:\n"
                                + "    - 숫자: 1\n"
                                + "    - 성별: [없음/남자/여자]\n"
                                + "    - 나이대: 20대\n"
                                + "    - 성격: [설명]\n"
                                + "    - 외형\n"
                                + "        - 헤어스타일: [설명]\n"
                                + "        - 눈 색깔: [설명]\n"
                                + "        - 체형: [설명]\n"
                                + "        - 피부 톤: [설명]\n"
                                + "4. 옷차림 설명:\n"
                                + "    - 상의: [설명]\n"
                                + "    - 하의: [설명]\n"
                                + "    - 신발: [설명]\n"
                                + "5. 배경\n"
                                + "    - 설명: 아무것도 없이 깨끗함\n"
                                + "    - 색깔: 하얀색"
                                + "6. 이미지 차원\n"
                                + "    - 차원: [2D/3D]\n"));
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
            ChatGptImageRequest request = new ChatGptImageRequest(imageGenerateQuery);
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
