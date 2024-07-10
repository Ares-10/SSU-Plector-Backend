package ssuPlector.repository.developer;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ssuPlector.domain.Developer;
import ssuPlector.domain.category.Part;

public interface DeveloperRepositoryCustom {

    Page<Developer> findDevelopers(String sortType, Part part, Pageable pageable);

    List<Developer> searchDeveloper(String developerName);
}
