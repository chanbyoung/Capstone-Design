package durikkiri.project.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindDto {
    private String email;
    private String loginId;
    private String username;
}
