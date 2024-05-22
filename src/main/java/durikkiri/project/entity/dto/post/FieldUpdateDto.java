package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.post.Field;
import durikkiri.project.entity.post.FieldCategory;
import durikkiri.project.entity.post.Post;
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
                .post(post)
                .fieldCategory(fieldCategory)
                .currentRecruitment(0)
                .totalRecruitment(totalRecruitment)
                .build();
    }
}
