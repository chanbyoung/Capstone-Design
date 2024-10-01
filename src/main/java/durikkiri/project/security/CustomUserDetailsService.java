package durikkiri.project.security;

import durikkiri.project.entity.Member;
import durikkiri.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        return memberRepository.findByLoginId(loginId)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당되는 회원을 찾을 수 없습니다."));
    }

    private UserDetails createUserDetails(Member member) {
        return CustomUserDetails.builder()
                .username(member.getLoginId())
                .password(member.getPassword())
                .nickName(member.getNickname())
                .authority(member.getRoles().isEmpty() ? "ROLE_USER" : member.getRoles().get(0)) // 권한 설정
                .enabled(member.isEnabled()) // 활성화 상태 설정
                .build();

    }
}
