package ssuPlector.controller;

import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ssuPlector.global.response.ApiResponse;
import ssuPlector.service.designer.DesignerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assistant/designer")
@Tag(name = "Designer AI 팀원 🤖", description = "Designer AI 팀원 기능 관련 API")
public class DesignerController {
    private final DesignerService designerService;

    @Operation(summary = "브랜딩", description = "입력받은 문자열을 기반으로 프로젝트의 브랜딩을 생성합니다._현근")
    @GetMapping("/branding")
    public ApiResponse<String> branding(@RequestParam(value = "projectInfo") String projectInfo) {
        return ApiResponse.onSuccess("브랜딩 생성 완료", designerService.branding(projectInfo));
    }

    @Operation(summary = "이미지 생성", description = "입력받은 문자열을 기반으로 프로젝트의 이미지를 생성합니다._현근")
    @GetMapping("/makeImage")
    public ApiResponse<String> makeImage(@RequestParam(value = "imageInfo") String imageInfo) {
        return ApiResponse.onSuccess("이미지 생성 완료", designerService.makeImage(imageInfo));
    }
}
