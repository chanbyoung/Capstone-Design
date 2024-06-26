package durikkiri.project.entity.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FieldCategory {
    FRONTEND("프론트엔드"), BACKEND("백엔드"), GENERAL("스터디원"), DESIGNER("디자이너");

    private final String value;

    @JsonCreator
    public static FieldCategory fromValue(String value) {
        for (FieldCategory category : FieldCategory.values()) {
            if (category.value.equals(value) || category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid value for FieldCategory: " + value);
    }

    @JsonValue
    public String toValue() {
        return this.value;
    }
}

