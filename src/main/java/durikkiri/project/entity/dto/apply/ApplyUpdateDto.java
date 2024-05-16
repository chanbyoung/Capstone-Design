package durikkiri.project.entity.dto.apply;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyUpdateDto {
    @NotBlank
    private String content;
}
