package ssuPlector.converter;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ssuPlector.domain.Image;
import ssuPlector.domain.Project;
import ssuPlector.domain.ProjectDeveloper;
import ssuPlector.domain.category.DevLanguage;
import ssuPlector.domain.category.DevTools;
import ssuPlector.domain.category.TechStack;
import ssuPlector.dto.request.ProjectDTO.ProjectRequestDTO;
import ssuPlector.dto.response.ProjectDTO.ProjectDetailDTO;
import ssuPlector.dto.response.ProjectDTO.ProjectListResponseDto;
import ssuPlector.dto.response.ProjectDTO.ProjectPreviewDTO;
import ssuPlector.dto.response.ProjectDTO.ProjectResponseDto;

@Component
public class ProjectConverter {

    public static ProjectPreviewDTO toProjectPreviewDTO(ProjectDeveloper projectDeveloper) {
        Project project = projectDeveloper.getProject();

        return ProjectPreviewDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .shortIntro(project.getShortIntro())
                .category(project.getCategory())
                .imageLink(project.getImageList().get(0).getImagePath())
                .build();
    }

    public static ProjectDetailDTO toProjectDetailDTO(Project project) {
        return ProjectDetailDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .imageLink(project.getImageList().get(0).getImagePath())
                .hits(project.getHits())
                .githubLink(project.getGithubLink())
                .developerList(
                        project.getProjectDeveloperList().stream()
                                .map(DeveloperConverter::toDeveloperPreviewDTO)
                                .collect(Collectors.toList()))
                .shortIntro(project.getShortIntro())
                .longIntro(project.getLongIntro())
                .category(project.getCategory())
                .infoPageLink(project.getInfoPageLink())
                .webLink(project.getWebLink())
                .appLink(project.getAppLink())
                .languageList(project.getLanguageList())
                .devToolList(project.getDevToolList())
                .techStackList(project.getTechStackList())
                .build();
    }

    public static Project toProject(
            ProjectRequestDTO requestDTO,
            ArrayList<DevLanguage> devLanguages,
            ArrayList<DevTools> devTools,
            ArrayList<TechStack> techStacks) {
        return Project.builder()
                .name(requestDTO.getName())
                .shortIntro(requestDTO.getShortIntro())
                .longIntro(requestDTO.getLongIntro())
                .infoPageLink(requestDTO.getInfoPageLink())
                .githubLink(requestDTO.getGithubLink())
                .webLink(requestDTO.getWebLink())
                .appLink(requestDTO.getAppLink())
                .category(requestDTO.getCategory())
                .languageList(devLanguages)
                .devToolList(devTools)
                .techStackList(techStacks)
                .build();
    }

    public static ProjectResponseDto toProjectResponseDto(Project project) {
        return ProjectResponseDto.builder()
                .id(project.getId())
                .name(project.getName())
                .imagePath(
                        project.getImageList() == null
                                ? null
                                : project.getImageList().stream()
                                        .filter(Image::getIsMainImage)
                                        .findFirst()
                                        .map(Image::getImagePath)
                                        .orElse(null))
                .shortIntro(project.getShortIntro())
                .category(project.getCategory().toString())
                .hits(project.getHits())
                .build();
    }

    public static ProjectListResponseDto toProjectListDto(Page<Project> projectPage) {
        return ProjectListResponseDto.builder()
                .totalPage(projectPage.getTotalPages())
                .currentElement(projectPage.getNumberOfElements())
                .totalElement(projectPage.getTotalElements())
                .projectResponseDtoList(
                        projectPage.getContent().stream()
                                .map(ProjectConverter::toProjectResponseDto)
                                .collect(Collectors.toList()))
                .build();
    }
}
