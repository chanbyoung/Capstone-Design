package durikkiri.project.entity.dto.apply;

import durikkiri.project.entity.Apply;
import durikkiri.project.entity.ApplyStatus;
import durikkiri.project.entity.post.FieldCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplyGetDto {
    private Long memberId;
    private FieldCategory fieldCategory;
    private ApplyStatus applyStatus;
    private String content;

    static public ApplyGetDto toDto(Apply apply) {
        return ApplyGetDto.builder()
                .memberId(apply.getMember().getId())
                .fieldCategory(apply.getFieldCategory())
                .applyStatus(apply.getApplyStatus())
                .content(apply.getContent())
                .build();
    }
}
