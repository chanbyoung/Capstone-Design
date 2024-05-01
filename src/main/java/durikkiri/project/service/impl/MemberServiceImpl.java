package durikkiri.project.service.impl;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.dto.member.MemberGetDto;
import durikkiri.project.entity.dto.member.SignInDto;
import durikkiri.project.entity.dto.member.SignUpDto;
import durikkiri.project.repository.MemberRepository;
import durikkiri.project.security.JwtToken;
import durikkiri.project.security.JwtTokenProvider;
import durikkiri.project.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    @Override
    @Transactional
    public HttpStatus signUp(SignUpDto signUpDto) {
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());
        ArrayList<String> roles = new ArrayList<>();
        roles.add("USER");
        Member member = memberRepository.save(signUpDto.toEntity(encodedPassword, roles));
        if (member.getId() != null) {
            return HttpStatus.OK;
        }
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public JwtToken signIn(SignInDto signInDto) {
        //1. username + password 를 기반 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(signInDto.getLoginId(), signInDto.getPassword());

        // 2. 실제 검증 authenticate() 메서드를 통해 요청된 Member에 대한 검증 진행
        // authenticate 메서드가 실행 될 때, CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3.인증 정보를 기반으로 JWT 토큰 생성
        return jwtTokenProvider.generateToken(authenticate);
    }

    @Override
    public MemberGetDto getMember(Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);

        return findMember.map(MemberGetDto::toDto).orElse(null);
    }
    @Override
    public MemberGetDto getMyInfo() {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Member> findMember = memberRepository.findByLoginId(memberLoginId);
        return findMember.map(MemberGetDto::toDto).orElse(null);
    }


}
