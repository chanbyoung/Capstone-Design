package durikkiri.project.service.impl;

import durikkiri.project.entity.Category;
import durikkiri.project.entity.Comment;
import durikkiri.project.entity.Image;
import durikkiri.project.entity.Post;
import durikkiri.project.entity.dto.HomeGetDto;
import durikkiri.project.entity.dto.comment.CommentDto;
import durikkiri.project.entity.dto.post.*;
import durikkiri.project.repository.CommentRepository;
import durikkiri.project.repository.ImageRepository;
import durikkiri.project.repository.PostRepository;
import durikkiri.project.service.PostService;
import durikkiri.project.repository.DslPostRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final DslPostRepository dslPostRepository;
    private final CommentRepository commentRepository;
    private final Validator validator;
    @Value("${file.dir}")
    private String fileDir;


    @Override
    @Transactional
    public HttpStatus addPost(PostAddDto postAddDto, MultipartFile image) throws IOException {
        HttpStatus badRequest = checkFieldValid(postAddDto);
        if (badRequest != null) return badRequest;
        Post savePost = postRepository.save(postAddDto.toEntity());
        if (image != null) {
            Image saveImage = imageRepository.save(Image.toEntity(image, fileDir, savePost));
            log.info("파일 저장 fullPath = {}", saveImage.getFullPath());
            image.transferTo(new File(saveImage.getFullPath()));
        }
        return OK;
    }

    private HttpStatus checkFieldValid(PostAddDto postAddDto) {
        if (!postAddDto.getCategory().equals(Category.GENERAL)) {
            if (postAddDto.getFieldList().isEmpty()) {
                return BAD_REQUEST;
            }
            for (FieldAddDto fieldAddDto : postAddDto.getFieldList()) {
                Set<ConstraintViolation<FieldAddDto>> violations = validator.validate(fieldAddDto);
                if (!violations.isEmpty()) {
                    // If any violations are found, return BAD_REQUEST
                    return HttpStatus.BAD_REQUEST;
                }
            }
        }
        return null;
    }

    @Override
    public Page<PostsGetDto> getPosts(Pageable pageable, PostSearchContent postSearchContent) {
        return dslPostRepository.getPosts(pageable, postSearchContent).map(PostsGetDto::toDto);
    }

    @Override
    @Transactional
    public PostGetDto getPost(Long postId, boolean flag) {
        return postRepository.findPostWithField(postId)
                .map(post -> {
                    if (flag) {
                        post.updateViewCount();
                    }
                    post.updateStatus();
                    return PostGetDto.toDto(post);
                })
                .orElse(null);
    }

    @Override
    @Transactional
    public HttpStatus updatePost(Long postId, MultipartFile image, PostUpdateDto postUpdateDto) {
        Optional<Post> findPost = postRepository.findPostWithField(postId);
        if (findPost.isPresent()) {
            try {
                Post post = findPost.get();
                post.updatePost(postUpdateDto);
                updateImage(image, post);
                return OK;
            } catch (IllegalArgumentException | IOException e) {
                return BAD_REQUEST;
            }
        }
        return NOT_FOUND;
    }

    private void updateImage(MultipartFile image, Post post) throws IOException {
        if (image != null) {
            if (post.getImage() != null) {
                Image existingImage = post.getImage();
                String existingImagePath = existingImage.getFullPath();
                File existingImageFile = new File(existingImagePath);
                if (existingImageFile.exists()) {
                    existingImageFile.delete();
                }
                Image newImage = Image.toEntity(image, fileDir, post);
                existingImage.updateImage(newImage);
                image.transferTo(new File(newImage.getFullPath()));
            }
            else {
                Image saveImage = imageRepository.save(Image.toEntity(image, fileDir, post));
                log.info("파일 저장 fullPath = {}", saveImage.getFullPath());
                image.transferTo(new File(saveImage.getFullPath()));
            }
        }
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

    @Override
    @Transactional
    public HttpStatus addComment(Long postId, CommentDto commentDto) {
        Optional<Post> findPost = postRepository.findById(postId);
        if (findPost.isPresent()) {
            Post post = findPost.get();
            post.updateComment(commentDto.toEntity(post));
            return OK;

        }
        return NOT_FOUND;
    }

    @Override
    @Transactional
    public HttpStatus updateComment(Long commentId, CommentDto commentDto) {
        Optional<Comment> findComment = commentRepository.findById(commentId);
        if (findComment.isPresent()) {
            findComment.get().updateComment(commentDto);
            return OK;
        }
        return NOT_FOUND;
    }

    @Override
    @Transactional
    public HttpStatus deleteComment(Long commentId) {
        Optional<Comment> findComment = commentRepository.findById(commentId);
        if (findComment.isPresent()) {
            commentRepository.delete(findComment.get());
            return OK;
        }
        return NOT_FOUND;
    }
}
