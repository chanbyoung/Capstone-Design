package durikkiri.project.entity.dto.apply;

import durikkiri.project.entity.Apply;
import durikkiri.project.entity.ApplyStatus;
import durikkiri.project.entity.post.Category;
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
    private String fieldCategory;
    private ApplyStatus applyStatus;
    private String content;

    static public ApplyGetDto toDto(Apply apply) {
        return ApplyGetDto.builder()
                .postTitle(apply.getRecruitmentInfo().getPost().getTitle())
                .postId(apply.getRecruitmentInfo().getPost().getId())
                .postCategory(apply.getRecruitmentInfo().getPost().getCategory())
                .memberName(apply.getMember().getNickname())
                .memberId(apply.getMember().getId())
                .fieldCategory(apply.getFieldCategory())
                .applyStatus(apply.getApplyStatus())
                .content(apply.getContent())
                .build();
    }
}
