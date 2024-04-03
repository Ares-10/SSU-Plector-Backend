package ssuPlector.controller;

import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ssuPlector.converter.DeveloperConverter;
import ssuPlector.domain.Developer;
import ssuPlector.dto.request.DeveloperDTO.DeveloperRequestDTO;
import ssuPlector.dto.response.DeveloperDTO.DeveloperDetailDTO;
import ssuPlector.global.response.ApiResponse;
import ssuPlector.service.developer.DeveloperService;
import ssuPlector.validation.annotation.ExistDeveloper;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/developers")
@Tag(name = "Developer 🖥️", description = "개발자 관련 API")
public class DeveloperController {
    private final DeveloperService developerService;

    // 개발자 생성
    @Operation(summary = "개발자 생성, 저장 API", description = "개발자를 생성합니다.")
    @PostMapping
    public ApiResponse createDeveloper(@RequestBody @Valid DeveloperRequestDTO requestDTO) {
        Long developerId = developerService.createDeveloper(requestDTO);
        return ApiResponse.onSuccess("개발자 생성 및 저장 완료.", developerId);
    }

    @Operation(summary = "개발자 상세조회 API", description = "개발자 프로필을 상세조회 합니다._숙희")
    @GetMapping("{developerId}")
    public ApiResponse<DeveloperDetailDTO> getDeveloperDetail(
            @ExistDeveloper @PathVariable("developerId") Long developerId) {
        Developer developer = developerService.getDeveloper(developerId);
        return ApiResponse.onSuccess(
                "개발자 상세조회 완료.", DeveloperConverter.toDeveloperDetailDTO(developer));
    }
}
