package ssuPlector.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ssuPlector.global.response.ApiResponse;
import ssuPlector.service.ai.PmServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pm")
@Tag(name = "PM AI 팀원", description = "PM AI 팀원 기능 관련 API")
public class PmController {

    private final PmServiceImpl pmService;

    @Operation(summary = "음성 회의록 정리", description = "음성 회의록을 정리 요약합니다.")
    @PostMapping(value = "/summary", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> summary(@RequestPart("file") MultipartFile file) {
        return ApiResponse.onSuccess("음성 회의록 정리 완료", pmService.summarize(file));
    }
}
