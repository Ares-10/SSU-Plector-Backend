package ssuPlector.service.pm;

import ssuPlector.dto.request.PmDTO.PmRequestDTO;

public interface PmService {
    String recommendMeeting(PmRequestDTO pmRequestDTO, long time, int numberOfParticipants);
}
