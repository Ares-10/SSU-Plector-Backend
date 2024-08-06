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
        return chatGptService.branding(projectInfo);
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
