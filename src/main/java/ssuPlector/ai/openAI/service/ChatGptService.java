package ssuPlector.ai.openAI.service;

public interface ChatGptService {

    String summarizeText(String text);

    String recommendMeetingToDo(String query);

    String branding(String query);
}
