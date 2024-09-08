package ssuPlector.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssuPlector.domain.category.Category;
import ssuPlector.domain.category.DevLanguage;
import ssuPlector.domain.category.DevTools;
import ssuPlector.domain.category.TechStack;
import ssuPlector.dto.response.DeveloperDTO.DeveloperPreviewDTO;

public class ProjectDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectPreviewDTO {
        Long id;
        String name;
        String imageLink;
        String shortIntro;
        Category category;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectDetailDTO {
        Long id;
        String name;
        String imageLink;
        List<DeveloperPreviewDTO> developerList;
        String shortIntro;
        String longIntro;
        Category category;
        long hits;
        String githubLink;
        String infoPageLink;
        String webLink;
        String appLink;
        List<DevLanguage> languageList;
        List<DevTools> devToolList;
        List<TechStack> techStackList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectListResponseDto {
        private int currentElement; // 현재 페이지 아이템 개수
        private int totalPage; // 전체 페이지
        private long totalElement; // 전체 아이템 개수
        private List<ProjectResponseDto> projectResponseDtoList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectResponseDto {
        Long id;
        String name;
        String imagePath;
        String shortIntro;
        String category;
        long hits;
    }
}
