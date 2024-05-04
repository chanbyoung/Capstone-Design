package durikkiri.project.entity.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateDto {
    @NotBlank
    private String nickname;
    @NotBlank
    private String major;
    private String content;
}
