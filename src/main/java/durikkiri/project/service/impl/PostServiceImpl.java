package durikkiri.project.service.impl;

import durikkiri.project.entity.Post;
import durikkiri.project.entity.dto.HomeGetDto;
import durikkiri.project.entity.dto.post.*;
import durikkiri.project.repository.PostRepository;
import durikkiri.project.service.PostService;
import durikkiri.project.repository.DslPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final DslPostRepository dslPostRepository;

    @Override
    @Transactional
    public HttpStatus addPost(PostAddDto postAddDto) {
        postRepository.save(postAddDto.toEntity());
        return OK;
    }

    @Override
    public Page<PostsGetDto> getPosts(Pageable pageable, PostSearchContent postSearchContent) {
        return dslPostRepository.getPosts(pageable, postSearchContent).map(PostsGetDto::toDto);
    }

    @Override
    @Transactional
    public PostGetDto getPost(Long postId, boolean flag) {
        return postRepository.findById(postId).map(post -> {
            if (flag) {
                post.updateViewCount();
            }
            return PostGetDto.toDto(post);
        }).orElse(null);
    }

    @Override
    @Transactional
    public HttpStatus updatePost(Long postId, PostUpdateDto postUpdateDto) {
        Optional<Post> findPost = postRepository.findById(postId);
        if (findPost.isPresent()) {
            findPost.get().updatePost(postUpdateDto);
            return OK;
        }
        return BAD_REQUEST;
    }

    @Override
    @Transactional
    public HttpStatus deletePost(Long postId) {
        Optional<Post> findPost = postRepository.findById(postId);
        if (findPost.isPresent()) {
            Post post = findPost.get();
            postRepository.delete(post);
            return OK;
        }
        return NOT_FOUND;
    }

    @Override
    public List<HomeGetDto> getHome() {
        return dslPostRepository.getHome().stream().map(HomeGetDto::toDto).toList();

    }
}
