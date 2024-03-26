package ssuPlector.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ssuPlector.converter.ProjectConverter;
import ssuPlector.domain.Project;
import ssuPlector.dto.response.ProjectDTO.ProjectDetailDTO;
import ssuPlector.global.response.ApiResponse;
import ssuPlector.service.project.ProjectService;
import ssuPlector.validation.annotation.ExistProject;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/projects")
@Tag(name = "Project", description = "프로젝트 관련 API")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "프로젝트 상세조회 API", description = "프로젝트를 상세조회 합니다.")
    @GetMapping("{projectId}")
    public ApiResponse<ProjectDetailDTO> getProjectDetail(
            @ExistProject @PathVariable("projectId") Long projectId) {
        Project project = projectService.getProject(projectId);
        return ApiResponse.onSuccess("프로젝트 상세조회 완료.", ProjectConverter.toProjectDetailDTO(project));
    }
}
