package durikkiri.project.dto.apply;

import durikkiri.project.entity.*;
import durikkiri.project.entity.post.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import static durikkiri.project.entity.ApplyStatus.*;

@Getter
@Builder
public class ApplyAddDto {
    @NotNull
    private String fieldCategory;
    @NotBlank
    private String content;

}
