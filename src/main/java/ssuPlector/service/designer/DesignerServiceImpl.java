package ssuPlector.service.designer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ssuPlector.ai.openAI.service.ChatGptService;

@Service
@RequiredArgsConstructor
public class DesignerServiceImpl implements DesignerService {
    @Value("${openai.model}")
    private String model;

    private final ChatGptService chatGptService;

    @Override
    public String branding(String projectInfo) {
        projectInfo =
                "브랜드나 아이디어, 프로젝트의 주제 설명 등을 입력할게. 브랜딩을 형식에 맞춰서 구체적으로 작성(추천)해줘.\n"
                        + "다음은 형식이야\n"
                        + "<기본 요소>\n"
                        + "1. 색상: [색상]\n"
                        + "2. 폰트: [폰트]\n"
                        + "3. 사운드: [사운드]\n"
                        + "4. 텍스처: [텍스처]\n"
                        + "5. 슬로건: [슬로건]\n"
                        + "<핵심 전략>\n"
                        + "1. 대상 고객 이해하기: [맞춤 설명]\n"
                        + "2. 앱 브랜딩을 위한 브랜드 아이덴티티 정의하기: [맞춤 설명]\n"
                        + "3. 브랜드 보이스 구축하기: [맞춤 설명]\n"
                        + "4. 포괄적인 마케팅 계획 세우기: [맞춤 설명]\n"
                        + "5. 사용자 경험에 집중하기: [맞춤 설명]\n"
                        + "지금부터 브랜드나 아이디어, 프로젝트의 주제 설명 등을 입력할게.\n"
                        + projectInfo;
        return chatGptService.standardChat(projectInfo);
    }

    @Override
    public String makeImage(String imageInfo) {
        imageInfo =
                "We're going to use open ai's image generation API, "
                        + "so we need to create a prompt in English to generate an image based on the text we enter. "
                        + "The input might look like this: "
                        + "<"
                        + imageInfo
                        + ">. "
                        + "Only give me the prompt.";
        String imageGenerateQuery = chatGptService.standardChat(imageInfo, model);
        return chatGptService.makeImage(imageGenerateQuery);
    }
}
