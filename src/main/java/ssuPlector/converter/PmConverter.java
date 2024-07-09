package ssuPlector.converter;

import java.util.List;

import org.springframework.stereotype.Component;

import ssuPlector.dto.response.PmDTO.PmResponseDTO;

@Component
public class PmConverter {
    public static PmResponseDTO toPmResponseDTO(List<Long> timelist) {
        return PmResponseDTO.builder()
                .introduceMyself(timelist.get(0))
                .iceBreaking(timelist.get(1))
                .brainstorming(timelist.get(2))
                .topicSelection(timelist.get(3))
                .progressSharing(timelist.get(4))
                .roleDivision(timelist.get(5))
                .troubleShooting(timelist.get(6))
                .feedback(timelist.get(7))
                .build();
    }
}
