package durikkiri.project.service.impl;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.Image;
import durikkiri.project.entity.dto.HomeGetDto;
import durikkiri.project.entity.dto.comment.CommentDto;
import durikkiri.project.entity.dto.post.*;
import durikkiri.project.entity.post.Category;
import durikkiri.project.entity.post.Comment;
import durikkiri.project.entity.post.Post;
import durikkiri.project.entity.post.RecruitmentInfo;
import durikkiri.project.entity.post.RecruitmentTechStack;
import durikkiri.project.entity.post.TechnologyStack;
import durikkiri.project.exception.*;
import durikkiri.project.repository.*;
import durikkiri.project.service.PostService;
import durikkiri.project.service.RecruitmentService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static durikkiri.project.entity.post.Category.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostServiceImpl implements PostService {

    private final static Long GUEST_USER = -1L;

    private final RecruitmentService recruitmentService;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    @Value("${file.dir}")
    private String fileDir;

    @Override
    @Transactional
    public void addPost(PostAddDto postAddDto, Long memberId, MultipartFile image)
            throws IOException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ForbiddenException("User not found"));

        // 게시글 저장
        Post savedPost = postRepository.save(postAddDto.toEntity(member));

        // 게시글이 일반 게시글이 아닌 경우 모집 정보 처리
        if (!postAddDto.isGeneralCategory()) {
            recruitmentService.processRecruitmentInfo(postAddDto.getRecruitmentAddDto(), savedPost);
        }

        // 이미지 처리
        if (image != null && !image.isEmpty()) {
            processImage(image, savedPost);
        }
    }

    @Override
    public Slice<PostsGetDto> getPosts(Pageable pageable, PostSearchContent postSearchContent) {
        return postRepository.getPostsByCursor(pageable, postSearchContent).map(PostsGetDto::toDto);
    }

    @Override
    @Transactional
    public GeneralPostGetDto getGeneralPost(Long postId, Long memberId, boolean flag) {
        Post post = postRepository.findPostWithMember(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        // 조회수 추가
        if (flag) post.updateViewCount();

        // 로그인 하지 않은 사용자의 경우 게시글만 반환
        if (memberId.equals(GUEST_USER)) {
            return GeneralPostGetDto.toDto(post, new PostUserStatusDto());
        }

        // 좋아요 표시 여부 및 작성자 여부
        PostUserStatusDto postUserStatusDto = getPostAuthInfo(memberId, postId,
                post.getMember().getId());

        return GeneralPostGetDto.toDto(post, postUserStatusDto);
    }

    @Override
    @Transactional
    public PostGetDto getPost(Long postId, Long memberId, boolean flag) {
        Post post = postRepository.findPostWithField(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        // 조회수 추가
        if (flag) post.updateViewCount();

        // 로그인 하지 않은 사용자의 경우 게시글만 반환
        if (memberId.equals(GUEST_USER)) {
            return PostGetDto.toDto(post, new PostUserStatusDto());
        }

        // 좋아요 표시 여부 및 작성자 여부
        PostUserStatusDto postUserStatusDto = getPostAuthInfo(memberId, postId,
                post.getMember().getId());

        return PostGetDto.toDto(post, postUserStatusDto);
    }



    @Override
    public List<HomeGetDto> getHome() {
        return postRepository.getHome(GENERAL).stream().map(HomeGetDto::toDto)
                .toList();
    }

    @Override
    public List<HomeGetDto> getLikePostList(Category category) {
        return postRepository.getLikePostList(category).stream().map(HomeGetDto::toDto).toList();
    }

    @Override
    @Transactional
    public void updatePost(Long postId, Long memberId, MultipartFile image,
            PostUpdateDto postUpdateDto) throws IOException {
        Post post = postRepository.findPostWithField(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        if (!post.getMember().getId().equals(memberId)) {
            throw new ForbiddenException("User not authorized to update this post");
        }
        if (postUpdateDto.getStartDate().isAfter(postUpdateDto.getEndDate())) {
            throw new BadRequestException("시작 날짜는 종료 날짜보다 이후일 수 없습니다.");
        }
        if (!postUpdateDto.getCategory().equals(GENERAL)) {
            recruitmentService.checkFieldValid(postUpdateDto.getFieldList());
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
    public void deletePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        if (!post.getMember().getId().equals(memberId)) {
            throw new ForbiddenException("User not authorized to delete this post");
        }
        if (post.getImage() != null) {
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

    /**
     * 이미지 파일을 저장합니다.
     */
    private void processImage(MultipartFile image, Post post) throws IOException {
        try {
            // Image 엔티티 생성 및 DB 저장
            Image savedImage = imageRepository.save(Image.toEntity(image, fileDir, post));
            log.info("Image entity saved with fullPath: {}", savedImage.getFullPath());

            // 파일 저장을 위한 경로 확인 및 디렉토리 생성
            File targetFile = new File(savedImage.getFullPath());
            if (!targetFile.getParentFile().exists() && !targetFile.getParentFile().mkdirs()) {
                throw new IOException("Failed to create directory for image storage");
            }

            // 실제 파일 저장
            image.transferTo(targetFile);
            log.info("Image file saved successfully at: {}", targetFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to process image for post id {}: {}", post.getId(), e.getMessage());
            throw e;
        }
    }

    private PostUserStatusDto getPostAuthInfo(Long memberId, Long postId,
            Long postOwnerId) {

        boolean isLiked = likeRepository.findByPostIdAndMemberId(postId, memberId)
                .isPresent();
        boolean isOwner = postOwnerId.equals(memberId);

        return new PostUserStatusDto(isLiked, isOwner);
    }

}
