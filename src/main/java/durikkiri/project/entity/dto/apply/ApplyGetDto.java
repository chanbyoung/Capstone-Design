package durikkiri.project.entity.dto.apply;

import durikkiri.project.entity.Apply;
import durikkiri.project.entity.ApplyStatus;
import durikkiri.project.entity.post.Category;
import durikkiri.project.entity.post.FieldCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplyGetDto {
    private String postTitle;
    private Long memberId;
    private String memberName;
    private Long postId;
    private Category postCategory;
    private FieldCategory fieldCategory;
    private ApplyStatus applyStatus;
    private String content;

    static public ApplyGetDto toDto(Apply apply) {
        return ApplyGetDto.builder()
                .postTitle(apply.getPost().getTitle())
                .postId(apply.getPost().getId())
                .postCategory(apply.getPost().getCategory())
                .memberName(apply.getMember().getNickname())
                .memberId(apply.getMember().getId())
                .fieldCategory(apply.getFieldCategory())
                .applyStatus(apply.getApplyStatus())
                .content(apply.getContent())
                .build();
    }
}
