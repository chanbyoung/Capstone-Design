package durikkiri.project.service;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.dto.ExistDto;
import durikkiri.project.entity.dto.member.MemberGetDto;
import durikkiri.project.entity.dto.member.MemberUpdateDto;
import durikkiri.project.entity.dto.member.SignInDto;
import durikkiri.project.entity.dto.member.SignUpDto;
import durikkiri.project.entity.post.Post;
import durikkiri.project.exception.AuthenticationException;
import durikkiri.project.exception.BadRequestException;
import durikkiri.project.repository.DslPostRepository;
import durikkiri.project.repository.MemberRepository;
import durikkiri.project.security.JwtToken;
import durikkiri.project.security.JwtTokenProvider;
import durikkiri.project.service.impl.MemberServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @InjectMocks
    private MemberServiceImpl memberServiceImpl;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private DslPostRepository postRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private MemberService memberService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private Authentication authentication;
    private final String testUser = "TESTUSER";
    private final String jwtToken = "testJwtToken";

    private Member member;

    @BeforeEach
    void setUp() {
        memberService = memberServiceImpl;
        SecurityContextHolder.setContext(securityContext);

        member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .loginId(testUser)
                .password("testPassword")
                .username(testUser)
                .build();
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(testUser);
        lenient().when(memberRepository.findByLoginId(testUser)).thenReturn(Optional.ofNullable(member));
    }


    @Test
    void signUp() {
        //given
        SignUpDto signUpDto = SignUpDto.builder()
                .loginId("testId")
                .password("testPassword")
                .build();
        Member member = Member.builder()
                .id(1L)
                .loginId("testId")
                .password("testPassword")
                .nickname("testNickName")
                .build();
        String encodedPassword = "encodedPassword";
        when(memberRepository.existsByLoginId(signUpDto.getLoginId())).thenReturn(Boolean.FALSE);
        when(passwordEncoder.encode(signUpDto.getPassword())).thenReturn(encodedPassword);
        when(memberRepository.save(signUpDto.toEntity(encodedPassword, any()))).thenReturn(member);

        //when
        memberService.signUp(signUpDto);

        //then
        verify(passwordEncoder, times(1)).encode(signUpDto.getPassword());
        verify(memberRepository, times(1)).save(signUpDto.toEntity(encodedPassword,any()));
    }

    @Test
    void testSignInSuccess() {
        //given
        SignInDto signInDto = new SignInDto();
        signInDto.setLoginId("testUser");
        signInDto.setPassword("testPassword");

        Authentication authentication = mock(Authentication.class);
        JwtToken expectedToken = mock(JwtToken.class);

        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn(expectedToken);

        //when
        JwtToken result = memberService.signIn(signInDto);

        //then
        assertEquals(expectedToken, result);
    }
    @Test
    public void testSignInFailure() {
        //given
        SignInDto signInDto = new SignInDto();
        signInDto.setLoginId("testUser");
        signInDto.setPassword("wrongPassword");

        //then
        Exception exception = assertThrows(AuthenticationException.class, () -> {
            memberService.signIn(signInDto);
        });
        assertEquals("Invalid login credentials", exception.getMessage());
    }

    @Test
    void getMyInfo() {
        //given
        Post mockPost = mock(Post.class);
        List<Post> mockPostList = List.of(mockPost);
        when(postRepository.progressProject(any())).thenReturn(mockPostList);
        when(postRepository.myRecruitingProject(any())).thenReturn(mockPostList);
        when(postRepository.myApplyProject(any())).thenReturn(mockPostList);

        //when
        MemberGetDto myInfo = memberService.getMyInfo();

        //then beforeEach 에서 정한 멤버 정보와 같은지 확인
        assertThat(myInfo.getId()).isEqualTo(1L);
        assertThat(myInfo.getUsername()).isEqualTo(testUser);
    }

    @Test
    void getMember() {
        //given
        when(memberRepository.findMemberByNickname(member.getNickname())).thenReturn(Optional.of(member));
        Post mockPost = mock(Post.class);
        List<Post> mockPostList = List.of(mockPost);
        when(postRepository.progressProject(any())).thenReturn(mockPostList);
        when(postRepository.myRecruitingProject(any())).thenReturn(mockPostList);

        //when
        MemberGetDto memberDto = memberService.getMember(member.getNickname());

        //then
        assertThat(memberDto.getId()).isEqualTo(1L);
        assertThat(memberDto.getUsername()).isEqualTo(testUser);
    }

    @Test
    void updateMember() {
        //given
        MemberUpdateDto memberUpdateDto = new MemberUpdateDto();
        memberUpdateDto.setMajor("컴퓨터공학과");

        //when
        memberService.updateMember(memberUpdateDto);

        //then
        assertThat(member.getMajor()).isEqualTo("컴퓨터공학과");
    }

    @Test
    void deleteMember() {
        //given
        //when
        memberService.deleteMember();

        //then
        verify(memberRepository, times(1)).delete(member);
    }

    @Test
    void findLoginIdByEmailAndUsername() {
        //given
        String email = member.getEmail();
        when(memberRepository.findMemberByEmail(email)).thenReturn(Optional.of(member));
        //when
        String loginId = memberService.findLoginIdByEmailAndUsername(email);

        //then
        assertThat(loginId).isEqualTo(member.getLoginId());
    }

    @Test
    void changePassword() {
        //given
        ExistDto existDto = new ExistDto();
        existDto.setEmail(member.getEmail());
        existDto.setNewPassword("changePassword");

        when(memberRepository.findMemberByEmail(existDto.getEmail())).thenReturn(Optional.of(member));
        when(passwordEncoder.encode(existDto.getNewPassword())).thenReturn(existDto.getNewPassword());

        //when
        memberService.changePassword(existDto);

        //then
        assertThat(member.getPassword()).isEqualTo(existDto.getNewPassword());
    }

    @Test
    void checkLoginIdDuplicate() {
        //given
        when(memberRepository.existsByLoginId(member.getLoginId())).thenReturn(Boolean.TRUE);

        //when
        Boolean flag = memberService.checkLoginIdDuplicate(member.getLoginId());

        //then
        assertThat(flag).isTrue();
    }

    @Test
    void logout() {
        // given
        long expiration = 1000L;
        when(jwtTokenProvider.getExpiration(jwtToken)).thenReturn(expiration);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        memberService.logout(jwtToken);

        // then
        verify(jwtTokenProvider).getExpiration(jwtToken);
        verify(valueOperations).set(eq(jwtToken), eq("blacklisted"), eq(expiration), eq(TimeUnit.MILLISECONDS));
    }


}