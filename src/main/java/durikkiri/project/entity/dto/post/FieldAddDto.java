package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.Field;
import durikkiri.project.entity.FieldCategory;
import durikkiri.project.entity.Post;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldAddDto {
    @NotNull
    private FieldCategory fieldCategory;
    @Positive
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
