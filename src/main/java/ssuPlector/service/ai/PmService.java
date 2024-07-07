package ssuPlector.service.ai;

import org.springframework.web.multipart.MultipartFile;

public interface PmService {

    String summarize(MultipartFile file);
}
