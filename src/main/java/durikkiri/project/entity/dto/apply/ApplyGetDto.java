package durikkiri.project.entity.dto.apply;

import durikkiri.project.entity.Apply;
import durikkiri.project.entity.ApplyStatus;
import durikkiri.project.entity.post.FieldCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplyGetDto {
    private String postTitle;
    private Long memberId;
    private Long postId;
    private FieldCategory fieldCategory;
    private ApplyStatus applyStatus;
    private String content;

    static public ApplyGetDto toDto(Apply apply) {
        return ApplyGetDto.builder()
                .postTitle(apply.getPost().getTitle())
                .postId(apply.getPost().getId())
                .memberId(apply.getMember().getId())
                .fieldCategory(apply.getFieldCategory())
                .applyStatus(apply.getApplyStatus())
                .content(apply.getContent())
                .build();
    }
}
