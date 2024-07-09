package ssuPlector.ai.openAi.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String role;
    private String content;
}
