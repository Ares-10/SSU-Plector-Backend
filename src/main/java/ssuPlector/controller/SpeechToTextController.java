package ssuPlector.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ssuPlector.global.response.ApiResponse;
import ssuPlector.service.ai.SpeechToTextService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/speechToText")
@Tag(name = "SpeechToText", description = "음성 회의록 정리 관련 API")
public class SpeechToTextController {

    private final SpeechToTextService speechToTextService;

    @Operation(summary = "음성 회의록 정리 API", description = "회의록을 정리 요약합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> convertSpeechToText(@RequestPart("file") MultipartFile file) {
        return ApiResponse.onSuccess("음성 회의록 정리 완료", speechToTextService.convertSpeechToText(file));
    }
}
