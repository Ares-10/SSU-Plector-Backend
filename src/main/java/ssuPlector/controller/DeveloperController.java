package ssuPlector.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ssuPlector.converter.DeveloperConverter;
import ssuPlector.domain.Developer;
import ssuPlector.dto.request.DeveloperDTO;
import ssuPlector.dto.request.DeveloperDTO.DeveloperListRequestDTO;
import ssuPlector.dto.request.DeveloperDTO.DeveloperMatchingDTO;
import ssuPlector.dto.request.DeveloperDTO.DeveloperRequestDTO;
import ssuPlector.dto.request.DeveloperDTO.DeveloperUpdateRequestDTO;
import ssuPlector.dto.response.DeveloperDTO.DeveloperDetailDTO;
import ssuPlector.dto.response.DeveloperDTO.DeveloperListResponseDTO;
import ssuPlector.dto.response.DeveloperDTO.DeveloperSearchDTO;
import ssuPlector.global.response.ApiResponse;
import ssuPlector.security.handler.annotation.AuthUser;
import ssuPlector.service.developer.DeveloperService;
import ssuPlector.validation.annotation.ExistDeveloper;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/developers")
@Tag(name = "Developer 🖥️", description = "개발자 관련 API")
public class DeveloperController {
    private final DeveloperService developerService;

    @Operation(summary = "개발자 프로필 생성", description = "개발자 프로필을 생성합니다._숙희")
    @PatchMapping("")
    public ApiResponse<Long> createDeveloper(
            @RequestParam(value = "email") String email,
            @RequestBody DeveloperRequestDTO requestDTO) {
        Long developerId = developerService.createDeveloper(email, requestDTO);
        return ApiResponse.onSuccess("개발자 프로필 생성 완료.", developerId);
    }

    @Operation(summary = "개발자 프로필 수정", description = "개발자 프로필을 수정합니다._찬민")
    @PatchMapping("/update")
    public ApiResponse<Long> updateDeveloper(
            @Parameter(name = "developer", hidden = true) @AuthUser Developer developer,
            @RequestBody DeveloperUpdateRequestDTO requestDTO) {
        Long developerId = developerService.updateDeveloper(developer.getId(), requestDTO);
        return ApiResponse.onSuccess("개발자 프로필 수정 완료.", developerId);
    }

    @Operation(summary = "개발자 상세조회 API", description = "개발자 프로필을 상세조회 합니다._숙희")
    @GetMapping("{developerId}")
    public ApiResponse<DeveloperDetailDTO> getDeveloperDetail(
            @ExistDeveloper @PathVariable("developerId") Long developerId) {
        Developer developer = developerService.getDeveloper(developerId, true);
        return ApiResponse.onSuccess(
                "개발자 상세조회 완료.", DeveloperConverter.toDeveloperDetailDTO(developer));
    }

    @Operation(summary = "개발자 리스트 조회", description = "개발자 리스트를 조회합니다._찬민")
    @GetMapping("/list")
    public ApiResponse<DeveloperListResponseDTO> getDeveloperList(
            @Valid @ModelAttribute DeveloperListRequestDTO requestDTO,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page) {
        Page<Developer> developerList = developerService.getDeveloperList(requestDTO, page);
        return ApiResponse.onSuccess(
                "개발자 리스트 조회 성공", DeveloperConverter.toDeveloperResponseListDTO(developerList));
    }

    @Operation(summary = "내 개발자 페이지 조회", description = "내 개발자 페이지를 조회합니다._숙희")
    @GetMapping("/mypage")
    public ApiResponse<DeveloperDetailDTO> getMyDeveloperPage(
            @Parameter(name = "developer", hidden = true) @AuthUser Developer developer) {
        Developer developerSelf = developerService.getDeveloper(developer.getId(), true);
        return ApiResponse.onSuccess(
                "내 개발자 페이지 조회 완료", DeveloperConverter.toDeveloperDetailDTO(developerSelf));
    }

    @Operation(summary = "더미 개발자 생성", description = "개발자 더미 데이터를 생성합니다._현근")
    @PostMapping(value = "/dummy", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Long> createDummyDeveloper(
            @RequestPart(value = "requestDTO") @Valid
                    DeveloperDTO.DummyDeveloperRequestDTO requestDTO,
            @RequestPart(value = "image", required = true) MultipartFile image) {
        Long developerId = developerService.createDummyDeveloper(requestDTO, image);
        return ApiResponse.onSuccess("더미 개발자 생성 완료.", developerId);
    }

    @Operation(summary = "개발자 검색", description = "개발자 이름을 입력받아 정보를 제공합니다._숙희")
    @GetMapping(value = "/search")
    public ApiResponse<List<DeveloperSearchDTO>> searchDeveloper(
            @RequestParam(value = "developerName") String developerName) {
        List<DeveloperSearchDTO> developerList = developerService.searchDeveloper(developerName);
        return ApiResponse.onSuccess("개발자 검색 완료.", developerList);
    }

    @Operation(
            summary = "개발자 매칭 봇",
            description =
                    "유저의 입력 값(개발자 파트, 기술스택, 사용언어, 최소 최대 학번(2자리), 프로젝트 경험 여부, 설명)을"
                            + "바탕으로 제일 높은 가중치의 개발자 3명 반환합니다._숙희")
    @GetMapping(value = "/match")
    public ApiResponse<List<DeveloperSearchDTO>> matchDeveloper(
            @Valid @ModelAttribute DeveloperMatchingDTO developerMatchingDTO,
            @RequestParam(value = "developerInfo") String developerInfo) {
        List<DeveloperSearchDTO> developerList =
                developerService.matchDeveloper(developerInfo, developerMatchingDTO);
        return ApiResponse.onSuccess("개발자 매칭 완료.", developerList);
    }

    @Operation(summary = "더미 개발자 프로필 수정", description = "더미 개발자 프로필을 수정합니다._숙희")
    @PatchMapping("/dummy/update")
    public ApiResponse<Long> updateDummyDeveloper(
            @RequestParam(value = "developerId") Long developer,
            @RequestBody DeveloperUpdateRequestDTO requestDTO) {
        Long developerId = developerService.updateDeveloper(developer, requestDTO);
        return ApiResponse.onSuccess("더미 개발자 프로필 수정 완료.", developerId);
    }
}
