package durikkiri.project.entity.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExistDto {
    private String email;
    private String code;
    private String newPassword;
}
