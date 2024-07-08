package ssuPlector.service.pm;

import java.lang.reflect.Field;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ssuPlector.ai.openAI.service.ChatGptService;
import ssuPlector.domain.category.MeetingTodo;
import ssuPlector.dto.request.PmDTO.PmRequestDTO;
import ssuPlector.global.exception.GlobalException;
import ssuPlector.global.response.code.GlobalErrorCode;

@Service
@RequiredArgsConstructor
public class PmServiceImpl implements PmService {
    private final ChatGptService chatGptService;

    @Override
    public String recommendMeeting(PmRequestDTO pmRequestDTO, long time, int numberOfParticipants) {
        long minutes = time / (1000 * 60);

        if (minutes == 0) {
            throw new GlobalException(GlobalErrorCode.TIME_TOO_SHORT);
        }
        if (numberOfParticipants == 0) {
            throw new GlobalException(GlobalErrorCode.NO_PARTICIPANTS);
        }

        Field[] fields = pmRequestDTO.getClass().getDeclaredFields();
        StringBuilder todo = new StringBuilder();
        StringBuilder todoFormat = new StringBuilder();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                boolean value = field.getBoolean(pmRequestDTO);
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

        return chatGptService.recommendMeetingToDo(query);
    }
}
