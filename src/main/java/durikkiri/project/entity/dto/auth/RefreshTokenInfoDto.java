package durikkiri.project.entity.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenInfoDto {

    private String account;
    private String refreshToken;
    private String authorities;
    private String nickName;

}
