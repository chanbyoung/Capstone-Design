package durikkiri.project.service.impl;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.Image;
import durikkiri.project.entity.dto.HomeGetDto;
import durikkiri.project.entity.dto.comment.CommentDto;
import durikkiri.project.entity.dto.post.*;
import durikkiri.project.entity.post.Category;
import durikkiri.project.entity.post.Comment;
import durikkiri.project.entity.post.Post;
import durikkiri.project.exception.*;
import durikkiri.project.repository.*;
import durikkiri.project.service.PostService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final LikeRepository likeRepository;
    private final DslPostRepository dslPostRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final Validator validator;
    @Value("${file.dir}")
    private String fileDir;

    @Override
    @Transactional
    public void addPost(PostAddDto postAddDto, MultipartFile image) throws IOException {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new ForbiddenException("User not found"));
        checkFieldValid(postAddDto);
        Post savePost = postRepository.save(postAddDto.toEntity(member));
        if (image != null) {
            Image saveImage = imageRepository.save(Image.toEntity(image, fileDir, savePost));
            log.info("File saved fullPath = {}", saveImage.getFullPath());
            image.transferTo(new File(saveImage.getFullPath()));
        }
    }

    private void checkFieldValid(PostAddDto postAddDto) {
        if (!postAddDto.getCategory().equals(Category.GENERAL)) {
            if (postAddDto.getFieldList().isEmpty()) {
                throw new BadRequestException("Field list is empty for non-general category");
            }
            for (FieldAddDto fieldAddDto : postAddDto.getFieldList()) {
                Set<ConstraintViolation<FieldAddDto>> violations = validator.validate(fieldAddDto);
                if (!violations.isEmpty()) {
                    String errorMessage = violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.joining(", "));
                    throw new BadRequestException("Field validation failed: "+errorMessage );
                }
            }
        }
    }

    @Override
    public Page<PostsGetDto> getPosts(Pageable pageable, PostSearchContent postSearchContent) {
        return dslPostRepository.getPosts(pageable, postSearchContent).map(PostsGetDto::toDto);
    }

    @Override
    @Transactional
    public PostGetDto getPost(Long postId, boolean flag) {
        Post post = postRepository.findPostWithField(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        if (flag) {
            post.updateViewCount();
        }
        if(!post.getCategory().equals(Category.GENERAL)) {
            post.updateStatus();
        }
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isLiked = false;
        if (!memberLoginId.equals("anonymousUser")) {
            isLiked = likeRepository.findByPostIdAndMemberId(postId, memberLoginId).isPresent();
        }
        boolean isOwner = post.getCreatedBy().equals(memberLoginId);
        return PostGetDto.toDto(post, isLiked, isOwner);
    }
    @Override
    public List<HomeGetDto> getHome() {
        return dslPostRepository.getHome().stream().map(HomeGetDto::toDto).toList();

    }
    @Override
    @Transactional
    public void updatePost(Long postId, MultipartFile image, PostUpdateDto postUpdateDto) throws IOException {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Post post = postRepository.findPostWithField(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!post.getCreatedBy().equals(memberLoginId)) {
            throw new ForbiddenException("User not authorized to update this post");
        }
        if (postUpdateDto.getStartDate().isAfter(postUpdateDto.getEndDate())) {
            throw new BadRequestException("시작 날짜는 종료 날짜보다 이후일 수 없습니다.");
        }
        post.updatePost(postUpdateDto);
        updateImage(image, post);
    }

    private void updateImage(MultipartFile image, Post post) throws IOException {
        if (image != null) {
            if (post.getImage() != null) {
                Image existingImage = post.getImage();
                deleteImage(existingImage);
                Image newImage = Image.toEntity(image, fileDir, post);
                existingImage.updateImage(newImage);
                image.transferTo(new File(newImage.getFullPath()));
            } else {
                Image saveImage = imageRepository.save(Image.toEntity(image, fileDir, post));
                log.info("File saved fullPath = {}", saveImage.getFullPath());
                image.transferTo(new File(saveImage.getFullPath()));
            }
        }
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        if (!post.getCreatedBy().equals(memberLoginId)) {
            throw new ForbiddenException("User not authorized to delete this post");
        }
        if(post.getImage() != null) {
            deleteImage(post.getImage());
        }
        postRepository.delete(post);
    }

    private static void deleteImage(Image image) {
        if (image != null) {
            String existingImagePath = image.getFullPath();
            File existingImageFile = new File(existingImagePath);
            if (existingImageFile.exists()) {
                existingImageFile.delete();
            }
        }
    }

    @Override
    @Transactional
    public void addComment(Long postId, CommentDto commentDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        post.updateComment(commentDto.toEntity(post));
    }

    @Override
    @Transactional
    public void updateComment(Long commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        comment.updateComment(commentDto);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        commentRepository.delete(comment);
    }

}
