package durikkiri.project.entity.dto.member;

import durikkiri.project.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class SignUpDto {
    private String username;
    private String nickname;
    private String loginId;
    private String password;
    private String email;
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
