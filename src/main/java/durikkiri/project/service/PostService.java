package durikkiri.project.service;

import durikkiri.project.entity.dto.HomeGetDto;
import durikkiri.project.entity.dto.comment.CommentDto;
import durikkiri.project.entity.dto.post.*;
import durikkiri.project.entity.post.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {

    void addPost(PostAddDto postAddDto, MultipartFile multipartFile) throws IOException;

    Slice<PostsGetDto> getPosts(Pageable pageable, PostSearchContent postSearchContent);

    PostGetDto getPost(Long postId, boolean flag);

    void updatePost(Long postId,MultipartFile multipartFile, PostUpdateDto postUpdateDto) throws IOException;

    void deletePost(Long postId);

    List<HomeGetDto> getLikePostList(Category category);

    void addComment(Long postId, CommentDto commentDto);

    void updateComment(Long commentId, CommentDto commentDto);

    void deleteComment(Long commentId);

    List<HomeGetDto> getHome();
}
