package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.Field;
import durikkiri.project.entity.FieldCategory;
import durikkiri.project.entity.Post;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldAddDto {
    private FieldCategory fieldCategory;
    private int totalRecruitment;

    public Field toValue() {
        return Field.builder()
                .fieldCategory(fieldCategory)
                .currentRecruitment(0)
                .totalRecruitment(totalRecruitment)
                .build();
    }
}
