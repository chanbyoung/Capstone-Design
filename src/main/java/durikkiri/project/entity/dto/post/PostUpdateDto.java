package durikkiri.project.entity.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateDto {
    @NotBlank
    private String title;
    @NotBlank
    private String content;

}
