package ssuPlector.ai.openAI.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatGptRequest {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatGptCommentRequest {

        private String model;
        private List<Message> messages;

        public ChatGptCommentRequest(String model, String prompt) {
            this.model = model;
            this.messages = new ArrayList<>();
            this.messages.add(new Message("user", prompt));
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatGptImageRequest {
        private String prompt;
        private int n;
        private String size;
    }
}
