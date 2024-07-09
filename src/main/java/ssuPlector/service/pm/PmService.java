package ssuPlector.service.pm;

import org.springframework.web.multipart.MultipartFile;

import ssuPlector.dto.request.PmDTO.PmRequestDTO;

public interface PmService {
    String recommendMeeting(PmRequestDTO pmRequestDTO, long time, int numberOfParticipants);

    String summarize(MultipartFile file);
}
