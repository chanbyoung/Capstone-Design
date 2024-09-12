package durikkiri.project.entity;

import durikkiri.project.entity.dto.member.MemberUpdateDto;
import durikkiri.project.entity.post.Comment;
import durikkiri.project.entity.post.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class Member implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String nickname;
    private String content;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "로그인 ID는 숫자와 영문자만 포함할 수 있습니다.")
    private String loginId;
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]*$",
            message = "비밀번호는 적어도 하나의 소문자와 하나의 숫자를 포함해야 합니다")
    private String password;

    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String major;
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @OneToMany(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Post> postList;

    @OneToMany(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Comment> commentList;

    @OneToMany(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Apply> appliesList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void updateMember(MemberUpdateDto memberUpdateDto) {
        this.nickname = memberUpdateDto.getNickname();
        this.major = memberUpdateDto.getMajor();
        this.content = memberUpdateDto.getContent();
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

}
