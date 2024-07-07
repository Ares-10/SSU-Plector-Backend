package ssuPlector.service.ai;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import ssuPlector.global.exception.GlobalException;
import ssuPlector.global.response.code.GlobalErrorCode;

@Service
@RequiredArgsConstructor
public class SpeechToTextService {

    private final NaverClovaService naverClovaService;
    private final ChatGptSummaryService chatGptSummaryService;

    public String convertSpeechToText(MultipartFile file) {

        StringBuilder totalText = new StringBuilder();

        try {
            File audioFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
            audioFile.createNewFile(); // 파일 생성
            FileOutputStream fos = new FileOutputStream(audioFile); // 파일 출력 스트림 생성
            fos.write(file.getBytes()); // 파일에 바이트 배열 쓰기
            fos.close(); // 출력 스트림 닫기

            List<File> chunks = splitAudio(audioFile, 60);

            for (File chunk : chunks) {
                String text = naverClovaService.soundToText(chunk);
                totalText.append(text).append(" ");
                chunk.delete();
            }

            audioFile.delete();
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }

        return chatGptSummaryService.summarize(totalText.toString().trim());
    }

    private List<File> splitAudio(File inputFile, int chunkDurationInSeconds) throws Exception {

        List<File> chunks = new ArrayList<>();
        String inputFilePath = inputFile.getAbsolutePath();
        String outputFilePattern = inputFilePath.replace(".m4a", "_chunk_%03d.m4a");

        // ffmpeg을 실행하여 오디오 파일 분할
        ProcessBuilder pb =
                new ProcessBuilder(
                        "ffmpeg",
                        "-hide_banner",
                        "-loglevel",
                        "error",
                        "-i",
                        inputFilePath,
                        "-f",
                        "segment",
                        "-segment_time",
                        String.valueOf(chunkDurationInSeconds),
                        "-c",
                        "copy",
                        outputFilePattern);
        pb.redirectErrorStream(true);
        pb.inheritIO();
        Process process = pb.start();
        process.waitFor();

        // 생성된 분할 파일 수집
        int index = 0;
        while (true) {
            File chunk = new File(String.format(outputFilePattern, index));
            if (!chunk.exists()) break;
            chunks.add(chunk);
            index++;
        }

        return chunks;
    }
}
