package ssuPlector.domain.category;

import lombok.Getter;

@Getter
public enum MeetingTodo {
    자기소개("introduceMyself"),
    아이스브레이킹("iceBreaking"),
    브레인스토밍("brainstorming"),
    주제선택("topicSelection"),
    역할분담("roleDivision"),
    진행상황공유("progressSharing"),
    트러블슈팅공유("troubleShooting"),
    피드백("feedback");

    private final String value;

    MeetingTodo(String value) {
        this.value = value;
    }

    public static MeetingTodo fromValue(String input) {
        for (MeetingTodo todo : MeetingTodo.values()) {
            if (todo.getValue().equalsIgnoreCase(input)) {
                return todo;
            }
        }
        throw new IllegalArgumentException();
    }
}
