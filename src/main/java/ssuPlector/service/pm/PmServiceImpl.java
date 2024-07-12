package ssuPlector.service.pm;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import ssuPlector.ai.naverCloud.service.ClovaService;
import ssuPlector.ai.openAI.service.ChatGptService;
import ssuPlector.domain.category.MeetingTodo;
import ssuPlector.dto.request.PmDTO.PmRequestDTO;
import ssuPlector.global.exception.GlobalException;
import ssuPlector.global.response.code.GlobalErrorCode;

@Service
@RequiredArgsConstructor
public class PmServiceImpl implements PmService {

    private final ChatGptService chatGptService;
    private final ClovaService clovaService;

    @Override
    public List<Long> recommendMeeting(
            PmRequestDTO pmRequestDTO, long time, int numberOfParticipants) {
        long minutes = time / (1000 * 60);

        if (minutes == 0) {
            throw new GlobalException(GlobalErrorCode.TIME_TOO_SHORT);
        }
        if (numberOfParticipants == 0) {
            throw new GlobalException(GlobalErrorCode.NO_PARTICIPANTS);
        }
        List<Boolean> meetingTodoList = new ArrayList<>();
        Field[] fields = pmRequestDTO.getClass().getDeclaredFields();
        StringBuilder todo = new StringBuilder();
        StringBuilder todoFormat = new StringBuilder();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                boolean value = field.getBoolean(pmRequestDTO);
                meetingTodoList.add(value);
                if (value) {
                    todo.append(MeetingTodo.fromValue(field.getName())).append("\n");
                    todoFormat.append(MeetingTodo.fromValue(field.getName())).append(":00분\n");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace(); // 필드 접근 예외
            }
        }

        String timeQuery = "회의 시간 : " + minutes + "분\n";
        String participantsQuery = "회의에 참여하는 인원 : " + numberOfParticipants + "명\n";
        String query =
                timeQuery
                        + participantsQuery
                        + todo
                        + "에 대해서 시간 분배를 해줘. 다른것 추가하지 말고 아래의 형식대로만 보내줘\n"
                        + todoFormat;

        String result = chatGptService.standardChat(query);

        List<Long> timeList = new ArrayList<>();
        String[] split = result.split("\n");
        int cnt = 0;
        for (Boolean meetingTodo : meetingTodoList) {
            if (!meetingTodo) {
                timeList.add(-1L);
            } else {
                String s = split[cnt];
                String second = s.replaceAll("[^0-9]", "");
                timeList.add(Long.parseLong(second) * 60000);
                cnt++;
            }
        }
        return timeList;
    }

    public String summarize(MultipartFile file) {

        StringBuilder totalText = new StringBuilder();

        try {
            File audioFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
            audioFile.createNewFile(); // 파일 생성
            FileOutputStream fos = new FileOutputStream(audioFile); // 파일 출력 스트림 생성
            fos.write(file.getBytes()); // 파일에 바이트 배열 쓰기
            fos.close(); // 출력 스트림 닫기

            List<File> chunks = splitAudio(audioFile, 60);

            for (File chunk : chunks) {
                String text = clovaService.soundToText(chunk);
                totalText.append(text).append(" ");
                chunk.delete();
            }

            audioFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }

        String text =
                "다음 텍스트를 프로젝트 회의록 형식에 맞게 요약해서 정리한 결과물을 반환해주세요. "
                        + "프로젝트 회의록 형식은 다음과 같아야 합니다:\n"
                        + "1. 회의 제목: [제목]\n"
                        + "2. 회의 날짜: [날짜]\n"
                        + "3. 참석자: [참석자 목록]\n"
                        + "4. 회의 목적: [목적]\n"
                        + "5. 진행 상황 공유: [진행 상황]\n"
                        + "6. 이슈 및 리스크: [이슈 및 리스크]\n"
                        + "7. 주요 논의 사항: [논의된 주요 사항들]\n"
                        + "8. 결론: [결론]\n"
                        + "9. 다음 단계: [향후 계획 및 액션 아이템]\n\n"
                        + totalText.toString().trim();
        return chatGptService.standardChat(text);
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
