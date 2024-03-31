package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.Post;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostGetDto {
    @NotBlank
    private String title;
    private String category;
    @NotBlank
    private String content;
    private Long viewCount;
    private Long likeCount;

    static public PostGetDto toDto(Post post) {
        return PostGetDto.builder()
                .title(post.getTitle())
                .category(post.getCategory().getValue())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .build();
    }
}
