package durikkiri.project.service;

import durikkiri.project.entity.dto.HomeGetDto;
import durikkiri.project.entity.dto.post.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface PostService {

    HttpStatus addPost(PostAddDto postAddDto);

    Page<PostsGetDto> getPosts(Pageable pageable, PostSearchContent postSearchContent);

    PostGetDto getPost(Long postId, boolean flag);

    HttpStatus updatePost(Long postId, PostUpdateDto postUpdateDto);

    HttpStatus deletePost(Long postId);

    List<HomeGetDto> getHome();
}
