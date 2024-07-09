package ssuPlector.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PmDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PmResponseDTO {
        private long introduceMyself;
        private long iceBreaking;
        private long brainstorming;
        private long topicSelection;
        private long progressSharing;
        private long roleDivision;
        private long troubleShooting;
        private long feedback;
    }
}
