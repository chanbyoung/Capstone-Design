package durikkiri.project.service;

import durikkiri.project.entity.Image;
import durikkiri.project.entity.Member;
import durikkiri.project.entity.dto.post.*;
import durikkiri.project.entity.post.Category;
import durikkiri.project.entity.post.Post;
import durikkiri.project.exception.BadRequestException;
import durikkiri.project.repository.*;
import durikkiri.project.security.CustomUserDetails;
import durikkiri.project.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @InjectMocks
    private PostServiceImpl postServiceImpl;
    @Mock
    private PostRepository postRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private CustomUserDetails customUserDetails;
    private PostService postService;
    private final String testUser = "TESTUSER";
    @BeforeEach
    void setUp() {
        postService = postServiceImpl;
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(customUserDetails);
        lenient().when(customUserDetails.getUsername()).thenReturn(testUser); // 여기에 로그인 ID 반환
        lenient().when(memberRepository.findByLoginId(testUser)).thenReturn(Optional.ofNullable(mock(Member.class)));
    }
    @Test
    void addPost() throws IOException {
        //given
        PostAddDto postAddDto = new PostAddDto();
        postAddDto.setCategory(Category.GENERAL);
        Post mockPost = postAddDto.toEntity(mock(Member.class));
        MultipartFile mockFile = mock(MultipartFile.class);
        Image mockImage = mock(Image.class);


        doNothing().when(mockFile).transferTo(new File("test"));
        when(mockFile.getOriginalFilename()).thenReturn("test");
        when(mockImage.getFullPath()).thenReturn("test");
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);
        when(imageRepository.save(any(Image.class))).thenReturn(mockImage);


        //when
        postService.addPost(postAddDto, customUserDetails, mockFile);

        //then
        verify(postRepository, times(1)).save(any(Post.class));
        verify(imageRepository, times(1)).save(any(Image.class));
        verify(mockFile, times(1)).transferTo(any(File.class));
    }

    @Test
    void failAddPostWhenProjectCategoryAndEmptyFieldList(){
        //given
        PostAddDto postAddDto = new PostAddDto();
        postAddDto.setCategory(Category.PROJECT);
        postAddDto.setFieldList(new ArrayList<>());

        //then
        assertThrows(BadRequestException.class,
                () -> postService.addPost(postAddDto, customUserDetails, null));

    }


    @Test
    void getPosts() {
        //given
        Pageable pageable = Pageable.unpaged();
        PostSearchContent postSearchContent = new PostSearchContent(null, null, null, null, null, null);
        List<Post> posts = Arrays.asList(new Post(), new Post());
        PageImpl<Post> postPage = new PageImpl<>(posts);

        when(postRepository.getPostsByCursor(any(Pageable.class), any(PostSearchContent.class))).thenReturn(postPage);

        //when
        Slice<PostsGetDto> result = postService.getPosts(pageable, postSearchContent);

        //then
        assertEquals(posts.size(),result.getContent().size());
    }

    @Test
    void getPost() {
        //given
        Post testPost = Post.builder()
                .id(1L)
                .title("testPost")
                .category(Category.GENERAL)
                .member(mock(Member.class))
                .commentList(new ArrayList<>())
                .fieldList(new ArrayList<>())
                .viewCount(0L)
                .build();
        when(postRepository.findPostWithField(1L)).thenReturn(Optional.of(testPost));
        when(customUserDetails.isAnonymous()).thenReturn(true); // 여기에 로그인 ID 반환


        //when
        PostGetDto result = postService.getPost(1L, customUserDetails, true);

        //then
        assertNotNull(result);
        verify(postRepository, times(1)).findPostWithField(1L);
    }

    @Test
    void updatePost() throws IOException {
        // given
        PostUpdateDto postUpdateDto = new PostUpdateDto();
        postUpdateDto.setStartDate(LocalDate.now());
        postUpdateDto.setEndDate(LocalDate.now().plusDays(1));
        postUpdateDto.setCategory(Category.GENERAL);

        Post mockPost = mock(Post.class);
        Member mockMember = mock(Member.class);
        when(postRepository.findPostWithField(anyLong())).thenReturn(Optional.of(mockPost));
        when(mockPost.getMember()).thenReturn(mockMember);
        when(mockMember.getLoginId()).thenReturn(testUser);

        // when
        postService.updatePost(1L, customUserDetails, null, postUpdateDto);

        // then
        verify(mockPost, times(1)).updatePost(postUpdateDto);
        verify(postRepository, times(1)).findPostWithField(anyLong());
    }

    @Test
    void deletePost() {
        //given
        Post mockPost = mock(Post.class);

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(mockPost));
        Member mockMember = mock(Member.class);
        when(mockPost.getMember()).thenReturn(mockMember);
        when(mockMember.getLoginId()).thenReturn(testUser);

        //when
        postService.deletePost(1L, customUserDetails);

        //then
        verify(postRepository, times(1)).delete(mockPost);
    }
}