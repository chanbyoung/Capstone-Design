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

    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final LikeRepository likeRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final TechnologyStackRepository technologyStackRepository;
    private final RecruitmentInfoTechStackRepository recruitmentInfoTechStackRepository;
    private final MemberRepository memberRepository;
    private final Validator validator;
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
            processRecruitmentInfo(postAddDto.getRecruitmentAddDto(), savedPost);
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
    public PostGetDto getPost(Long postId, Long memberId, boolean flag) {
        Post post = postRepository.findPostWithField(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        if (flag) {
            post.updateViewCount();
        }
        if (!post.getCategory().equals(GENERAL)) {
            post.getRecruitmentInfo().updateStatus();
        }
        if (memberId.equals(GUEST_USER)) {
            return PostGetDto.toDto(post, new PostUserStatusDto(null, null));
        }
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
            checkFieldValid(postUpdateDto.getFieldList());
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
     * 모집 정보를 처리합니다.
     */
    private void processRecruitmentInfo(RecruitmentAddDto recruitmentAddDto, Post post) {
        // 필드 리스트 유효성 검사
        checkFieldValid(recruitmentAddDto.getFieldList());

        // 기술 스택 리스트 처리 (조회 후 존재하지 않는 경우 새로 생성)
        List<TechnologyStack> technologyStacks = getOrCreateTechnologyStacks(
                recruitmentAddDto.getTechnologyStackList());

        // 모집 정보 저장
        RecruitmentInfo savedRecruitment = recruitmentRepository.save(
                recruitmentAddDto.toEntity(post));
        log.info("Recruitment info created for post id: {}", post.getId());

        // 매핑 테이블 저장
        saveRecruitmentInfoTechStack(technologyStacks, savedRecruitment);
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


    private void saveRecruitmentInfoTechStack(List<TechnologyStack> saveTechnologyStacks,
            RecruitmentInfo saveRecruitment) {
        List<RecruitmentTechStack> saveList = saveTechnologyStacks.stream()
                .map(techStack -> RecruitmentTechStack.builder()
                        .recruitmentInfo(saveRecruitment)
                        .technologyStack(techStack)
                        .build())
                .toList();

        recruitmentInfoTechStackRepository.saveAll(saveList);
    }

    /**
     * 모집 정보에 포함된 Field 리스트의 유효성을 검사합니다.
     */
    private void checkFieldValid(List<FieldDto> fields) {
        if (fields == null || fields.isEmpty()) {
            throw new BadRequestException("Field list is empty for non-general category");
        }
        for (FieldDto field : fields) {
            Set<ConstraintViolation<FieldDto>> violations = validator.validate(field);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", "));
                throw new BadRequestException("Field validation failed: " + errorMessage);
            }
        }
    }


    /**
     * 기술 스택 리스트를 받아 DB에 존재하는 스택은 조회하고, 존재하지 않는 경우 새 엔티티를 생성 및 저장한 후 전체 리스트를 반환합니다.
     */
    public List<TechnologyStack> getOrCreateTechnologyStacks(List<String> technologyStackNames) {
        // DB에 존재하는 기술 스택 조회
        List<TechnologyStack> existingStacks = technologyStackRepository.findByNameIn(
                technologyStackNames);
        Set<String> existingNames = existingStacks.stream()
                .map(TechnologyStack::getName)
                .collect(Collectors.toSet());

        // 존재하지 않는 기술 스택을 찾아서 새 엔티티 생성
        List<TechnologyStack> newStacks = technologyStackNames.stream()
                .filter(name -> !existingNames.contains(name))
                .map(TechnologyStack::new)
                .collect(Collectors.toList());

        // 새 엔티티가 있다면 DB에 저장 후 전체 리스트에 추가
        if (!newStacks.isEmpty()) {
            List<TechnologyStack> savedStacks = technologyStackRepository.saveAll(newStacks);
            existingStacks.addAll(savedStacks);
            log.info("Created new TechnologyStacks: {}", savedStacks);
        }
        return existingStacks;
    }

    private PostUserStatusDto getPostAuthInfo(Long memberId, Long postId,
            Long postOwnerId) {

        boolean isLiked = likeRepository.findByPostIdAndMemberId(postId, memberId)
                .isPresent();
        boolean isOwner = postOwnerId.equals(memberId);

        return new PostUserStatusDto(isLiked, isOwner);
    }

}
