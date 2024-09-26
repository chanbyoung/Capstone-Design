package durikkiri.project.entity.dto.member;

import durikkiri.project.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class SignUpDto {
    @NotBlank
    private String username;
    @NotBlank
    private String nickname;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "로그인 ID는 숫자와 영문자만 포함할 수 있습니다.")
    private String loginId;
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]*$",
            message = "비밀번호는 적어도 하나의 소문자와 하나의 숫자를 포함해야 합니다")
    private String password;
    @NotBlank
    private String email;
    @NotBlank
    private String major;
    private List<String> roles;

    public Member toEntity(String encodedPassword, List<String> roles) {
        return Member.builder()
                .username(username)
                .nickname(nickname)
                .loginId(loginId)
                .password(encodedPassword)
                .email(email)
                .major(major)
                .roles(roles)
                .postList(new ArrayList<>())
                .commentList(new ArrayList<>())
                .appliesList(new ArrayList<>())
                .build();

    }

}
