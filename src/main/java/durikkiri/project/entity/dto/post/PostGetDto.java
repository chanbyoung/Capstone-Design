package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.dto.comment.CommentGetDto;
import durikkiri.project.entity.post.Post;
import durikkiri.project.entity.post.TechnologyStack;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Builder
public class PostGetDto {
    @NotBlank
    private String title;
    private String category;
    @NotBlank
    private String content;
    private Long postId;
    private Long memberId;
    private String memberNickname;
    private List<TechnologyStack> technologyStackList;
    private List<CommentGetDto> commentList;
    private List<FieldGetDto> fieldList;
    private ImageGetDto image; //이미지의 URL이 저장된필드
    private Long viewCount;
    private Long likeCount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isLiked;
    private Boolean isOwner;

    static public PostGetDto toDto(Post post, PostUserStatusDto postUserStatusDto) {
        // Post 엔티티 내의 Field 리스트를 FieldGetDto 리스트로 변환
        List<FieldGetDto> fieldGetDtoList = post.getFieldList().stream()
                .map(FieldGetDto::toDto) // 각 Field 엔티티를 FieldGetDto로 변환
                .collect(Collectors.toList());

        List<CommentGetDto> commentGetDtoList = post.getCommentList().stream()
                .map(CommentGetDto::toDto)
                .toList();

        return PostGetDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .category(post.getCategory().getValue())
                .content(post.getContent())
                .memberId(post.getMember().getId())
                .memberNickname(post.getMember().getNickname())
                .technologyStackList(post.getTechnologyStackList())
                .commentList(commentGetDtoList)
                .fieldList(fieldGetDtoList) // 변환된 FieldGetDto 리스트 설정
                .image(Optional.ofNullable(post.getImage()).map(ImageGetDto::toDto).orElse(null))
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .startDate(post.getStartDate())
                .endDate(post.getEndDate())
                .isLiked(postUserStatusDto.getLiked())
                .isOwner(postUserStatusDto.getOwner())
                .build();
    }
}
