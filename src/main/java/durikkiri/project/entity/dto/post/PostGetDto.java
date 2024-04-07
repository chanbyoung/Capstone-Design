package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.Field;
import durikkiri.project.entity.Post;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PostGetDto {
    @NotBlank
    private String title;
    private String category;
    @NotBlank
    private String content;
    private List<FieldGetDto> fieldList;
    private Long viewCount;
    private Long likeCount;

    static public PostGetDto toDto(Post post) {
        // Post 엔티티 내의 Field 리스트를 FieldGetDto 리스트로 변환
        List<FieldGetDto> fieldGetDtoList = post.getFieldList().stream()
                .map(FieldGetDto::toDto) // 각 Field 엔티티를 FieldGetDto로 변환
                .collect(Collectors.toList());

        return PostGetDto.builder()
                .title(post.getTitle())
                .category(post.getCategory().getValue())
                .content(post.getContent())
                .fieldList(fieldGetDtoList) // 변환된 FieldGetDto 리스트 설정
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .build();
    }
}
