package ssuPlector.service.project;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ssuPlector.domain.Project;
import ssuPlector.global.exception.GlobalException;
import ssuPlector.global.response.code.GlobalErrorCode;
import ssuPlector.redis.service.ProjectHitsService;
import ssuPlector.repository.project.ProjectRepository;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectHitsService projectHitsService;

    @Override
    public Project getProject(Long projectId) {
        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.PROJECT_NOT_FOUND));
        projectHitsService.incrementHits(projectId);
        return project;
    }

    @Override
    @Transactional
    public void updateProjectHits(Long projectId, Long hit) {
        projectRepository.incrementHitsById(projectId, hit);
    }

    @Override
    public List<Long> getUpdateTargetProjectIds(List<Long> projectIdList) {
        return projectRepository.findAllByIdIn(projectIdList).stream().map(Project::getId).toList();
    }

    @Override
    public boolean existsByProjectId(Long id) {
        return projectRepository.existsById(id);
    }
}
