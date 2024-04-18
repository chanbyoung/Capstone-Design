package durikkiri.project.entity.dto.apply;

import durikkiri.project.entity.*;
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

    public Apply toEntity(Post post) {
        return post.getFieldList().stream()
                .filter(field -> field.getFieldCategory().equals(fieldCategory))
                .findFirst()
                .map(field -> Apply.builder()
                        .fieldCategory(fieldCategory)
                        .post(post)
                        .content(content)
                        .applyStatus(UNREAD)
                        .build())
                .orElse(null);
    }

}
