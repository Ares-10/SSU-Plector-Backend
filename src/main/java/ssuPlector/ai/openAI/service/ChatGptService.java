package ssuPlector.ai.openAI.service;

public interface ChatGptService {
    String standardChat(String query);

    String standardChat(String query, String model);

    String makeImage(String query);

    String branding(String projectInfo);
}
