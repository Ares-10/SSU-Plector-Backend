package ssuPlector.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PmDTO {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PmRequestDTO {
        private boolean introduceMyself;
        private boolean iceBreaking;
        private boolean brainstorming;
        private boolean topicSelection;
        private boolean progressSharing;
        private boolean roleDivision;
        private boolean troubleShooting;
        private boolean feedback;
    }
}
