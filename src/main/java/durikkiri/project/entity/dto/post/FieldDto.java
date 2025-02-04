package durikkiri.project.entity.dto.post;


import durikkiri.project.entity.post.Field;
import durikkiri.project.entity.post.Post;
import durikkiri.project.entity.post.RecruitmentInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldDto {
    @NotNull(message = "필드 값을 선택해주세요")
    private String fieldCategory;
    @Positive
    private int totalRecruitment;

    public Field toEntity(RecruitmentInfo recruitmentInfo) {
        return Field.builder()
                .fieldCategory(fieldCategory)
                .recruitmentInfo(recruitmentInfo)
                .currentRecruitment(0)
                .totalRecruitment(totalRecruitment)
                .build();
    }
}
