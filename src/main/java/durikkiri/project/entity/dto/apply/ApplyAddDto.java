package durikkiri.project.entity.dto.apply;

import durikkiri.project.entity.*;
import durikkiri.project.entity.post.FieldCategory;
import durikkiri.project.entity.post.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import static durikkiri.project.entity.ApplyStatus.*;

@Getter
@Builder
public class ApplyAddDto {
    @NotNull
    private FieldCategory fieldCategory;
    @NotBlank
    private String content;

    public Apply toEntity(Post post,Member member) {
        return post.getFieldList().stream()
                .filter(field -> field.getFieldCategory().equals(fieldCategory))
                .findFirst()
                .map(field -> Apply.builder()
                        .fieldCategory(fieldCategory)
                        .post(post)
                        .member(member)
                        .content(content)
                        .applyStatus(UNREAD)
                        .build())
                .orElse(null);
    }

}
