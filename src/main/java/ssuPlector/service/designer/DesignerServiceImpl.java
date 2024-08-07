package ssuPlector.service.designer;

import org.springframework.beans.factory.annotation.Value;
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
        return chatGptService.makeImage(imageInfo);
    }
}
