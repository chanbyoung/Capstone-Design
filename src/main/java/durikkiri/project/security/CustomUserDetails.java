package durikkiri.project.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Builder
@Getter
public class CustomUserDetails implements UserDetails {
    private String username;
    private String password;
    private String nickName;
    private String authority;
    private boolean enabled;
    private boolean anonymous;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority(authority));
        return auth;
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

    public static CustomUserDetails createAnonymousUser() {
        return CustomUserDetails.builder()
                .username("anonymousUser")
                .anonymous(true) // 플래그를 통해 익명 사용자로 설정
                .build();
    }

}
