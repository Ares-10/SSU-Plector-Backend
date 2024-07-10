package ssuPlector.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ssuPlector.converter.PmConverter;
import ssuPlector.dto.request.PmDTO.PmRequestDTO;
import ssuPlector.dto.response.PmDTO.PmResponseDTO;
import ssuPlector.global.response.ApiResponse;
import ssuPlector.service.pm.PmService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assistant/pm")
@Tag(name = "PM AI 팀원 🤖", description = "PM AI 팀원 기능 관련 API")
public class PmController {
    private final PmService pmService;

    @Operation(summary = "회의 진행", description = "입력 값을 받아 회의 진행을 추천합니다._숙희")
    @GetMapping("/meeting")
    public ApiResponse<PmResponseDTO> recommendMeetingProgress(
            @ModelAttribute PmRequestDTO pmRequestDTO,
            @RequestParam(value = "time") long time,
            @RequestParam(value = "numberOfParticipants") int numberOfParticipants) {
        List<Long> meeting = pmService.recommendMeeting(pmRequestDTO, time, numberOfParticipants);
        return ApiResponse.onSuccess("회의 진행 추천 완료", PmConverter.toPmResponseDTO(meeting));
    }

    @Operation(summary = "음성 회의록 정리", description = "음성 회의록을 정리 요약합니다._찬민")
    @PostMapping(value = "/summary", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> summary(@RequestPart("file") MultipartFile file) {
        return ApiResponse.onSuccess("음성 회의록 정리 완료", pmService.summarize(file));
    }
}
