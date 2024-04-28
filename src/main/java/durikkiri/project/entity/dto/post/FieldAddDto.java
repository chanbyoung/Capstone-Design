package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.post.Field;
import durikkiri.project.entity.post.FieldCategory;
import durikkiri.project.entity.post.Post;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldAddDto {
    private FieldCategory fieldCategory;
    private int totalRecruitment;

    public Field toEntity(Post post) {
        return Field.builder()
                .fieldCategory(fieldCategory)
                .post(post)
                .currentRecruitment(0)
                .totalRecruitment(totalRecruitment)
                .build();
    }
}
