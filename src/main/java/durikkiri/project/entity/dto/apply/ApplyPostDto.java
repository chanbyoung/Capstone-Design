package durikkiri.project.entity.dto.apply;

import durikkiri.project.entity.ApplyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyPostDto {
    @NotNull
    private ApplyStatus applyStatus;
}
