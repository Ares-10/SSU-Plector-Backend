package ssuPlector.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ssuPlector.dto.request.PmDTO.PmRequestDTO;
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
    public ApiResponse<String> recommendMeetingProgress(
            @ModelAttribute PmRequestDTO pmRequestDTO,
            @RequestParam(value = "time") long time,
            @RequestParam(value = "numberOfParticipants") int numberOfParticipants) {
        String meeting = pmService.recommendMeeting(pmRequestDTO, time, numberOfParticipants);
        return ApiResponse.onSuccess("회의 진행 추천 완료", meeting);
    }
}
