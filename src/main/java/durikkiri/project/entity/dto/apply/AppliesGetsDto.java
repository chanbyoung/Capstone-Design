package durikkiri.project.entity.dto.apply;

import durikkiri.project.entity.Apply;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AppliesGetsDto {
    private Long id;
    private String memberName;
    private LocalDateTime createdAt;

    public static AppliesGetsDto toDto(Apply apply) {
        return AppliesGetsDto.builder()
                .id(apply.getId())
                .memberName(apply.getCreatedBy())
                .createdAt(apply.getCreatedAt())
                .build();

    }
}
