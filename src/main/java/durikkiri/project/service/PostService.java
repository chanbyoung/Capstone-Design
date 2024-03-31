package durikkiri.project.service;

import durikkiri.project.entity.dto.post.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.awt.print.Pageable;

public interface PostService {

    HttpStatus addPost(PostAddDto postAddDto);

    Page<PostsGetDto> getPosts(Pageable pageable, PostSearchContent postSearchContent);

    PostGetDto getPost(Long postId);

    HttpStatus updatePost(Long postId, PostUpdateDto postUpdateDto);

    HttpStatus deletePost(Long postId);
}
