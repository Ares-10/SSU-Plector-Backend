package ssuPlector.service.project;

import static ssuPlector.dto.request.ProjectDTO.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import ssuPlector.domain.Project;
import ssuPlector.dto.request.ProjectDTO.ProjectListRequestDto;

public interface ProjectService {

    Long createProject(ProjectRequestDTO requestDTO, MultipartFile image);

    Project getProject(Long projectId);

    void updateProjectHits(Long projectId, Long hit);

    List<Long> getUpdateTargetProjectIds(List<Long> projectIdList);

    boolean existsByProjectId(Long id);

    Page<Project> getProjectList(ProjectListRequestDto requestDto, int page);

    Long updateProject(Long projectId, ProjectUpdateRequestDTO requestDTO);
}
