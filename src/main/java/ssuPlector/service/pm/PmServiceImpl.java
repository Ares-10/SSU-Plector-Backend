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
import ssuPlector.ai.naverCloud.service.ClovaSpeechService;
import ssuPlector.ai.openAI.service.ChatGptService;
import ssuPlector.domain.category.MeetingTodo;
import ssuPlector.dto.request.PmDTO.PmRequestDTO;
import ssuPlector.global.exception.GlobalException;
import ssuPlector.global.response.code.GlobalErrorCode;

@Service
@RequiredArgsConstructor
public class PmServiceImpl implements PmService {

    private final ChatGptService chatGptService;
    private final ClovaSpeechService clovaSpeechService;

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

        String result = chatGptService.recommendMeetingToDo(query);

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

            totalText.append(clovaSpeechService.convert(audioFile));

            audioFile.delete();
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode._INTERNAL_SERVER_ERROR);
        }

        return chatGptService.summarizeText(totalText.toString().trim());
    }
}
