package durikkiri.project.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyUpdateDto {
    @NotBlank
    private String content;
}
