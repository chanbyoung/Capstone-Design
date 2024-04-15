package durikkiri.project.entity.dto.apply;

import durikkiri.project.entity.Apply;
import durikkiri.project.entity.Field;
import durikkiri.project.entity.FieldCategory;
import durikkiri.project.entity.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplyAddDto {
    private FieldCategory fieldCategory;
    private String content;

    public Apply toEntity(Post post) {
        return post.getFieldList().stream()
                .filter(field -> field.getFieldCategory().equals(fieldCategory))
                .findFirst()
                .map(field -> Apply.builder()
                        .fieldCategory(fieldCategory)
                        .post(post)
                        .content(content)
                        .build())
                .orElse(null);
    }

}
