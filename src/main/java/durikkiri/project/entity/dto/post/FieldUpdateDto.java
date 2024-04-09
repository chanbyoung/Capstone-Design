package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.Field;
import durikkiri.project.entity.FieldCategory;
import durikkiri.project.entity.Post;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FieldUpdateDto {
    private FieldCategory fieldCategory;
    private int totalRecruitment;
    public Field toEntity(Post post) {
        return Field.builder()
                .fieldCategory(fieldCategory)
                .currentRecruitment(0)
                .totalRecruitment(totalRecruitment)
                .post(post)
                .build();
    }
}
