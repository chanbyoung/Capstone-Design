package durikkiri.project.entity.dto.apply;

import durikkiri.project.entity.Apply;
import durikkiri.project.entity.post.FieldCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AppliesGetsDto {
    private Long id;
    private String postTitle;
    private FieldCategory fieldCategory; // 지원 분야
    private String memberName;
    private LocalDateTime createdAt;

    public static AppliesGetsDto toDto(Apply apply) {
        return AppliesGetsDto.builder()
                .id(apply.getId())
                .postTitle(apply.getPost().getTitle())
                .fieldCategory(apply.getFieldCategory())
                .memberName(apply.getCreatedBy())
                .createdAt(apply.getCreatedAt())
                .build();

    }
}
