package durikkiri.project.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplyStatus {
    ACCEPT("지원 수락"),REJECT("지원 반려"),UNREAD("확인 중"),READ("확인 완료");
    private final String value;
}
