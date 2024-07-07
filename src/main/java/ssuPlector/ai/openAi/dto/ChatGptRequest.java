package ssuPlector.ai.openAi.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ChatGptRequest {
    private String model;
    private List<Message> messages;

    public ChatGptRequest(String model, String text) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("user", text));
    }
}
