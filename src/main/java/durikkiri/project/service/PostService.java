package durikkiri.project.service;

import durikkiri.project.dto.HomeGetDto;
import durikkiri.project.dto.post.GeneralPostGetDto;
import durikkiri.project.dto.post.PostAddDto;
import durikkiri.project.dto.post.PostGetDto;
import durikkiri.project.dto.post.PostSearchContent;
import durikkiri.project.dto.post.PostUpdateDto;
import durikkiri.project.dto.post.PostsGetDto;
import durikkiri.project.entity.post.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {

    void addPost(PostAddDto postAddDto, Long memberId, MultipartFile multipartFile) throws IOException;

    Slice<PostsGetDto> getPosts(Pageable pageable, PostSearchContent postSearchContent);

    PostGetDto getPost(Long postId, Long memberId, boolean flag);

     GeneralPostGetDto getGeneralPost(Long postId, Long memberId, boolean flag);

     void updatePost(Long postId, Long memberId, MultipartFile multipartFile, PostUpdateDto postUpdateDto) throws IOException;

    void deletePost(Long postId, Long memberId);

    List<HomeGetDto> getLikePostList(Category category);


    List<HomeGetDto> getHome();
}
