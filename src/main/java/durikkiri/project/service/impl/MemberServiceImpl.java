package durikkiri.project.service.impl;
import durikkiri.project.entity.Member;
import durikkiri.project.entity.dto.member.MemberGetDto;
import durikkiri.project.entity.dto.member.MemberUpdateDto;
import durikkiri.project.entity.dto.member.SignInDto;
import durikkiri.project.entity.dto.member.SignUpDto;
import durikkiri.project.entity.post.Post;
import durikkiri.project.exception.AuthenticationException;
import durikkiri.project.exception.BadRequestException;
import durikkiri.project.exception.NotFoundException;
import durikkiri.project.repository.DslPostRepository;
import durikkiri.project.repository.MemberRepository;
import durikkiri.project.security.JwtToken;
import durikkiri.project.security.JwtTokenProvider;
import durikkiri.project.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final DslPostRepository dslPostRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void signUp(SignUpDto signUpDto) {
        if (memberRepository.existsByLoginId(signUpDto.getLoginId())) {
            throw new BadRequestException("Login ID already exists");
        }
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());
        ArrayList<String> roles = new ArrayList<>();
        roles.add("USER");
        Member member = memberRepository.save(signUpDto.toEntity(encodedPassword, roles));
        if (member.getId() == null) {
            throw new RuntimeException("Failed to sign up member");
        }
    }

    @Override
    @Transactional
    public JwtToken signIn(SignInDto signInDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(signInDto.getLoginId(), signInDto.getPassword());
        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            return jwtTokenProvider.generateToken(authentication);
        } catch (Exception e) {
            throw new AuthenticationException("Invalid login credentials");
        }
    }

    @Override
    public JwtToken refreshAccessToken(String refreshToken) {
        return jwtTokenProvider.refreshAccessToken(refreshToken);
    }

    @Override
    public void logout(String jwtToken) {
        long expiration = jwtTokenProvider.getExpiration(jwtToken);
        redisTemplate.opsForValue().set(jwtToken, "blacklisted", expiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public MemberGetDto getMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found"));
        List<Post> progressProject = dslPostRepository.progressProject(member);
        List<Post> recruitingProject = dslPostRepository.myRecruitingProject(member);
        return MemberGetDto.toDto(member, progressProject, recruitingProject, null);
    }

    @Override
    public MemberGetDto getMyInfo() {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new NotFoundException("Member not found"));
        List<Post> progressProject = dslPostRepository.progressProject(member);
        List<Post> recruitingProject = dslPostRepository.myRecruitingProject(member);
        List<Post> myApplyProject = dslPostRepository.myApplyProject(member);
        return MemberGetDto.toDto(member, progressProject, recruitingProject, myApplyProject);
    }

    @Override
    @Transactional
    public void updateMember(MemberUpdateDto memberUpdateDto) {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new NotFoundException("Member not found"));
        member.updateMember(memberUpdateDto);
    }

    @Override
    @Transactional
    public void deleteMember() {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new NotFoundException("Member not found"));
        memberRepository.delete(member);
    }
    @Override
    public String findLoginIdByEmailAndUsername(String email) {
        return memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 회원입니다.")).getLoginId();
    }

    @Override
    @Transactional
    public void changePassword(String email, String newPassword) {
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 회원입니다."));
        member.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Override
    public Boolean checkLoginIdDuplicate(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    @Override
    public Boolean checkNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(nickname);

    }

}
