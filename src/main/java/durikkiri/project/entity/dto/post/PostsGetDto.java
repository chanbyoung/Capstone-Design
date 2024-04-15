package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.Post;
import durikkiri.project.entity.TechnologyStack;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
public class PostsGetDto {
    private Long id;
    private String title;
    private List<TechnologyStack> technologyStackList;
    private Long viewCount;
    private Long likeCount;
    static public PostsGetDto toDto(Post post) {
        return PostsGetDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .technologyStackList(post.getTechnologyStackList())
                .likeCount(post.getLikeCount())
                .viewCount(post.getViewCount()).build();
    }

}
