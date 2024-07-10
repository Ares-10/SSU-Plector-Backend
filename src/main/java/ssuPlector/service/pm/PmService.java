package ssuPlector.service.pm;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import ssuPlector.dto.request.PmDTO.PmRequestDTO;

public interface PmService {
    List<Long> recommendMeeting(PmRequestDTO pmRequestDTO, long time, int numberOfParticipants);

    String summarize(MultipartFile file);
}
