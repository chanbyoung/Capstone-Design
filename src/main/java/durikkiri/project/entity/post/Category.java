package durikkiri.project.entity.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    PROJECT("프로젝트"), STUDY("스터디"), GENERAL("자유글");
    private final String value;

    @JsonCreator
    public static Category fromValue(String value) {
        for (Category category : Category.values()) {
            if (category.value.equals(value) || category.name().equals(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid value for Category: " + value);
    }

    @JsonValue
    public String toValue() {
        return this.value;
    }

}
