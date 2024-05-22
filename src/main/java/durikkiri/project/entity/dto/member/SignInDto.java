package durikkiri.project.entity.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInDto {
    @NotBlank
    private String loginId;
    @NotBlank
    private String password;

}
