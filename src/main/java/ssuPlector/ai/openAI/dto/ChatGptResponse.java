package ssuPlector.ai.openAI.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatGptResponse {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatGptCommentResponse {
        private List<Choice> choices;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Choice {
            private int index;
            private Message message;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatGptImageResponse {

        private long created;
        private List<ImageURL> data;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ImageURL {
            private String url;
        }
    }
}
