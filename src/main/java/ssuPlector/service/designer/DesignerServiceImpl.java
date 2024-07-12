package ssuPlector.service.designer;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ssuPlector.ai.openAI.service.ChatGptService;

@Service
@RequiredArgsConstructor
public class DesignerServiceImpl implements DesignerService {

    private final ChatGptService chatGptService;

    @Override
    public String branding(String projectInfo) {
        projectInfo =
                "브랜드나 아이디어, 프로젝트의 주제 설명을 입력할게. 색깔, 슬로건, 특장점 등을 포함한 브랜딩을 작성해줘."
                        + "서론 문구 등 불필요한 문구는 제외하고 작성해줘\n"
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
        String imageGenerateQuery = chatGptService.standardChat(imageInfo, "gpt-4o");
        return chatGptService.makeImage(imageGenerateQuery);
    }
}
