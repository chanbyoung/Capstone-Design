package durikkiri.project.service;

import durikkiri.project.entity.Apply;
import durikkiri.project.entity.ApplyStatus;
import durikkiri.project.entity.Member;
import durikkiri.project.entity.dto.apply.AppliesGetsDto;
import durikkiri.project.entity.dto.apply.ApplyAddDto;
import durikkiri.project.entity.dto.apply.ApplyGetDto;
import durikkiri.project.entity.dto.apply.ApplyUpdateDto;
import durikkiri.project.entity.post.Field;
import durikkiri.project.entity.post.FieldCategory;
import durikkiri.project.entity.post.Post;
import durikkiri.project.entity.post.RecruitmentStatus;
import durikkiri.project.exception.BadRequestException;
import durikkiri.project.repository.ApplyRepository;
import durikkiri.project.repository.MemberRepository;
import durikkiri.project.repository.PostRepository;
import durikkiri.project.service.impl.ApplyServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplyServiceTest {
    @InjectMocks
    private ApplyServiceImpl applyServiceImpl;
    @Mock
    private ApplyRepository applyRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    private ApplyService applyService;
    private final String testUser = "TESTUSER";
    private Member member;

    @BeforeEach
    void setUp() {
        applyService = applyServiceImpl;
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
    void getApplies() {
        //given
        Apply apply = Apply.builder()
                .id(1L)
                .post(mock(Post.class))
                .member(member)
                .build();
        List<Apply> applyList = List.of(apply);
        when(applyRepository.findApply(member.getId())).thenReturn(applyList);
        //when
        List<AppliesGetsDto> applies = applyService.getApplies();

        //then
        assertThat(applies.size()).isEqualTo(1);
        verify(applyRepository, times(1)).findApply(member.getId());
    }

    @Test
    void getMyApplies() {
        //given
        Apply apply = Apply.builder()
                .id(1L)
                .post(mock(Post.class))
                .member(member)
                .build();
        List<Apply> applyList = List.of(apply);
        when(applyRepository.findMyApply(member)).thenReturn(applyList);

        //when
        List<AppliesGetsDto> myApplies = applyService.getMyApplies();

        //then
        assertThat(myApplies.size()).isEqualTo(1);
        verify(applyRepository,times(1)).findMyApply(member);
    }

    @Test
    void addApply() {
        //given
        Post post = Post.builder()
                .id(1L)
                .member(mock(Member.class))
                .status(RecruitmentStatus.Y)
                .fieldList(new ArrayList<>())
                .build();
        ApplyAddDto applyAddDto = ApplyAddDto.builder()
                .fieldCategory(FieldCategory.BACKEND)
                .content("testContent")
                .build();
        when(postRepository.findPostWithField(post.getId())).thenReturn(Optional.of(post));

        //when
        applyService.addApply(1L, applyAddDto);

        //then
        verify(applyRepository, times(1)).save(any());
    }

    /**
     * 자신의 게시글에 지원하는 경우
     */
    @Test
    void failAddApply_ApplyToOwnPost() {
        //given
        Post post = Post.builder()
                .id(1L)
                .member(member)
                .status(RecruitmentStatus.Y)
                .fieldList(new ArrayList<>())
                .build();
        ApplyAddDto applyAddDto = ApplyAddDto.builder()
                .fieldCategory(FieldCategory.BACKEND)
                .content("testContent")
                .build();
        when(postRepository.findPostWithField(post.getId())).thenReturn(Optional.of(post));

        //when
        Throwable exception = assertThrows(BadRequestException.class, () -> {
            applyService.addApply(1L, applyAddDto);
        });

        //then
        assertEquals("You cannot apply to your own post", exception.getMessage());
    }

    /**
     * 모집이 완료된 게시글에 지원하는 경우
     */
    @Test
    void failAddApply_RecruitmentCompleted() {
        //given
        Post post = Post.builder()
                .id(1L)
                .member(mock(Member.class))
                .status(RecruitmentStatus.N)
                .fieldList(new ArrayList<>())
                .build();
        ApplyAddDto applyAddDto = ApplyAddDto.builder()
                .fieldCategory(FieldCategory.BACKEND)
                .content("testContent")
                .build();
        when(postRepository.findPostWithField(post.getId())).thenReturn(Optional.of(post));

        //when
        Throwable exception = assertThrows(BadRequestException.class, () -> {
            applyService.addApply(1L, applyAddDto);
        });

        //then
        assertEquals("이미 모집이 완료된 게시글입니다.", exception.getMessage());
    }

    /**
     * 중복 지원하는 경우
     */
    @Test
    void addApply_DuplicateApplication() {
        //given
        Post post = Post.builder()
                .id(1L)
                .member(mock(Member.class))
                .status(RecruitmentStatus.Y)
                .fieldList(new ArrayList<>())
                .build();
        ApplyAddDto applyAddDto = ApplyAddDto.builder()
                .fieldCategory(FieldCategory.BACKEND)
                .content("testContent")
                .build();
        when(postRepository.findPostWithField(post.getId())).thenReturn(Optional.of(post));
        when(applyRepository.existsByPostAndMember(post, member)).thenReturn(true);

        //when
        Throwable exception = assertThrows(BadRequestException.class, () -> {
            applyService.addApply(1L, applyAddDto);
        });

        //then
        assertEquals("You have already applied to this post", exception.getMessage());
    }



    @Test
    void getApply() {
        //given
        Apply apply = Apply.builder()
                .id(1L)
                .post(mock(Post.class))
                .member(member)
                .applyStatus(ApplyStatus.UNREAD)
                .build();
        when(applyRepository.findById(apply.getId())).thenReturn(Optional.of(apply));

        //when
        ApplyGetDto applyDto = applyService.getApply(apply.getId());

        //then
        assertThat(applyDto.getApplyStatus()).isEqualTo(ApplyStatus.READ);
    }

    @Test
    void acceptApply() {
        //given
        Field field = Field.builder()
                .fieldCategory(FieldCategory.BACKEND)
                .currentRecruitment(0)
                .totalRecruitment(1)
                .build();
        Post post = Post.builder()
                .member(member)
                .fieldList(List.of(field))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .status(RecruitmentStatus.Y)
                .build();
        Apply apply = Apply.builder()
                .id(1L)
                .post(post)
                .fieldCategory(FieldCategory.BACKEND)
                .member(Mockito.mock(Member.class))
                .applyStatus(ApplyStatus.UNREAD)
                .build();
        when(applyRepository.findApplyWithPost(apply.getId())).thenReturn(Optional.of(apply));

        //when
        applyService.acceptApply(1L, ApplyStatus.ACCEPT);

        //then
        assertThat(apply.getApplyStatus()).isEqualTo(ApplyStatus.ACCEPT);
        assertThat(field.getCurrentRecruitment()).isEqualTo(1);
        assertThat(post.getStatus()).isEqualTo(RecruitmentStatus.N);
    }

    /**
     * 이미 수락 요청이 된 지원서에 다시 지원 수락 요청을 하는 경우
     */
    @Test
    void failAcceptApply() {
        //given
        Post post = Post.builder()
                .member(member)
                .fieldList(new ArrayList<>())
                .status(RecruitmentStatus.Y)
                .build();
        Apply apply = Apply.builder()
                .id(1L)
                .post(post)
                .member(Mockito.mock(Member.class))
                .applyStatus(ApplyStatus.ACCEPT)
                .build();
        when(applyRepository.findApplyWithPost(apply.getId())).thenReturn(Optional.of(apply));

        //then
        assertThrows(BadRequestException.class, () ->
                applyService.acceptApply(apply.getId(), ApplyStatus.ACCEPT));
    }

    @Test
    void cancelApply() {
        //given
        Field field = Field.builder()
                .fieldCategory(FieldCategory.BACKEND)
                .currentRecruitment(1)
                .totalRecruitment(1)
                .build();
        Post post = Post.builder()
                .member(member)
                .fieldList(List.of(field))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .status(RecruitmentStatus.N)
                .build();
        Apply apply = Apply.builder()
                .id(1L)
                .post(post)
                .fieldCategory(FieldCategory.BACKEND)
                .member(Mockito.mock(Member.class))
                .applyStatus(ApplyStatus.ACCEPT)
                .build();
        when(applyRepository.findApplyWithPost(apply.getId())).thenReturn(Optional.of(apply));

        // when
        applyService.cancelApply(apply.getId());

        //then
        assertThat(apply.getApplyStatus()).isEqualTo(ApplyStatus.REJECT);
        assertThat(field.getCurrentRecruitment()).isEqualTo(0);
        assertThat(post.getStatus()).isEqualTo(RecruitmentStatus.Y);

    }

    @Test
    void updateApply() {
        Apply apply = Apply.builder()
                .id(1L)
                .member(member)
                .content("testContent")
                .applyStatus(ApplyStatus.UNREAD)
                .build();
        ApplyUpdateDto applyUpdateDto = new ApplyUpdateDto();
        applyUpdateDto.setContent("updateContent");
        when(applyRepository.findById(apply.getId())).thenReturn(Optional.of(apply));

        //when
        applyService.updateApply(apply.getId(), applyUpdateDto);

        //then
        assertThat(apply.getContent()).isEqualTo(applyUpdateDto.getContent());
    }

    @Test
    void deleteApply() {
        //given
        Apply apply = Apply.builder()
                .id(1L)
                .member(member)
                .applyStatus(ApplyStatus.UNREAD)
                .build();
        when(applyRepository.findById(apply.getId())).thenReturn(Optional.of(apply));

        //when
        applyService.deleteApply(apply.getId());

        //then
        verify(applyRepository,times(1)).delete(apply);
    }

}