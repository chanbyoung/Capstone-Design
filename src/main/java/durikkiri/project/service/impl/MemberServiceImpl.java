package durikkiri.project.service.impl;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.dto.member.MemberGetDto;
import durikkiri.project.entity.dto.member.SignInDto;
import durikkiri.project.entity.dto.member.SignUpDto;
import durikkiri.project.entity.post.Post;
import durikkiri.project.repository.DslPostRepository;
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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final DslPostRepository dslPostRepository;
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
        if (findMember.isPresent()) {
            Member member = findMember.get();
            List<Post> progressProject = dslPostRepository.progressProject(member);
            List<Post> recruitingProject = dslPostRepository.myRecruitingProject(member);
            return MemberGetDto.toDto(member, progressProject, recruitingProject,null);
        }
        return null;
    }
    @Override
    public MemberGetDto getMyInfo() {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Member> findMember = memberRepository.findByLoginId(memberLoginId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            //진행중인 프로젝트
            List<Post> progressProject = dslPostRepository.progressProject(member);
            //내가 구인하는 프로젝트
            List<Post> recruitingProject = dslPostRepository.myRecruitingProject(member);
            //내가 지원한 프로젝트
            List<Post> myApplyProject = dslPostRepository.myApplyProject(member);
            return MemberGetDto.toDto(member, progressProject,recruitingProject,myApplyProject);
        }
        return null;
    }



}
