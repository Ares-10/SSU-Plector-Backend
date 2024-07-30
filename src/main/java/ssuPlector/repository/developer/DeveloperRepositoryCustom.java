package ssuPlector.repository.developer;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ssuPlector.domain.Developer;
import ssuPlector.domain.category.Part;
import ssuPlector.dto.request.DeveloperDTO.DeveloperMatchingDTO;

public interface DeveloperRepositoryCustom {

    Page<Developer> findDevelopers(String sortType, Part part, Pageable pageable);

    List<Developer> searchDeveloper(String developerName);

    Map<Long, Double> matchDeveloper(List<Developer> developers, DeveloperMatchingDTO requestDTO);

    List<Developer> essentialMatchDeveloper(DeveloperMatchingDTO requestDTO);
}
