package durikkiri.project.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplyStatus {
    ACCEPT("지원 수락"),REJECT("지원 반려"),UNREAD("확인 중"),READ("확인 완료");
    private final String value;

    @JsonCreator
    public static ApplyStatus fromValue(String value) {
        for (ApplyStatus status : ApplyStatus.values()) {
            if (status.value.equals(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid value for ApplyStatus: " + value);
    }

    @JsonValue
    public String toValue() {
        return this.value;
    }
}
