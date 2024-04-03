package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.Field;
import durikkiri.project.entity.FieldCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class FieldGetDto {
    private String fieldCategory;
    private int currentRecruitment;
    private int totalRecruitment;

    static public FieldGetDto toDto(Field field) {
        return FieldGetDto.builder()
                .fieldCategory(field.getFieldCategory().getValue())
                .currentRecruitment(field.getCurrentRecruitment())
                .totalRecruitment(field.getTotalRecruitment())
                .build();
    }
}
