package ssuPlector.service.developer;

import static ssuPlector.dto.request.DeveloperDTO.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import ssuPlector.domain.Developer;
import ssuPlector.dto.response.DeveloperDTO.DeveloperSearchDTO;

public interface DeveloperService {
    Long createDeveloper(String email, DeveloperRequestDTO requestDTO);

    Long updateDeveloper(Long id, DeveloperUpdateRequestDTO requestDTO);

    Developer getDeveloper(Long id, boolean isHit);

    boolean existsByDeveloperId(Long id);

    void updateDeveloperHits(Long developerId, Long hit);

    List<Long> getUpdateTargetDeveloperIds(List<Long> DeveloperIdList);

    Page<Developer> getDeveloperList(DeveloperListRequestDTO requestDTO, int page);

    Long createDummyDeveloper(DummyDeveloperRequestDTO requestDTO, MultipartFile image);

    List<DeveloperSearchDTO> searchDeveloper(String developerName);
}
