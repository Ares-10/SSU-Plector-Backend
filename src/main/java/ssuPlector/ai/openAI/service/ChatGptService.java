package ssuPlector.ai.openAI.service;

public interface ChatGptService {

    String summarizeText(String text);

    String recommendMeetingToDo(String query);

    String standardChat(String query);

    String standardChat(String query, String model);

    String makeImage(String query);
}
